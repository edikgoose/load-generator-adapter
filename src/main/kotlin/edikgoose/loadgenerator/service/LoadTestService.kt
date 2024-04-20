package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.GrafanaProperties
import edikgoose.loadgenerator.converter.toLoadTestOutputDto
import edikgoose.loadgenerator.dto.LoadTestOutputDto
import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.entity.Scenario
import edikgoose.loadgenerator.enumeration.LoadTestStage
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.enumeration.SystemConfigurationType
import edikgoose.loadgenerator.exception.NotFoundException
import edikgoose.loadgenerator.exception.SessionAlreadyStoppedException
import edikgoose.loadgenerator.exception.YandexTankException
import edikgoose.loadgenerator.feign.YandexTankApiFeignClient
import edikgoose.loadgenerator.repository.ScenarioRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Сервис для управления нагрузочными тестами
 */
@Service
class LoadTestService(
    val yandexTankApiFeignClient: YandexTankApiFeignClient,
    val loadTestDbService: LoadTestDbService,
    val yandexTankTestConfigService: YandexTankTestConfigService,
    val scenarioRepository: ScenarioRepository,
    val grafanaProperties: GrafanaProperties,
    val systemConfigurationService: SystemConfigurationService,
) {
    val logger: Logger = LoggerFactory.getLogger(LoadTestService::class.java)

    @Transactional
    fun runLoadTest(
        scenarioId: Long,
        name: String
    ): LoadTestOutputDto {
        val scenario = scenarioRepository.findById(scenarioId).orElseThrow {
            NotFoundException(
                scenarioId,
                Scenario::class.java
            )
        }

        val loadTest = loadTestDbService.saveLoadTest(
            LoadTest(
                id = null,
                scenario = scenario,
                name = name,
                status = LoadTestStatus.CREATED,
                externalId = null,
                createdDate = null,
                finishDate = null,
                stage = null
            )
        )

        logger.info("Config for Yandex tank:\n${scenario.yandexTankConfig}")

        val yandexTankTestRunOutputDto =
            if (scenario.ammo != null) { // если указан файл для патрон, значит запустить тест мы должны через break stage
                loadTest.status = LoadTestStatus.LOCKED
                yandexTankApiFeignClient.runLoadTest(
                    yandexTankTestConfigService.substitutePrefixMeasurement(
                        scenario.yandexTankConfig!!,
                        loadTest.id!!.toString()
                    ),
                    breakStage = "init"
                )
            } else {
                loadTest.status = LoadTestStatus.CREATED
                yandexTankApiFeignClient.runLoadTest(
                    yandexTankTestConfigService.substitutePrefixMeasurement(
                        scenario.yandexTankConfig!!,
                        loadTest.id!!.toString()
                    )
                )
            }

        loadTest.externalId = yandexTankTestRunOutputDto.session

        loadTestDbService.saveLoadTest(loadTest)
        logger.info("Load test has successfully started. External id: ${loadTest.externalId}")

        if (loadTest.scenario.systemConfiguration?.type == SystemConfigurationType.CONSUL) {
            systemConfigurationService.pollConfiguration(loadTestId = loadTest.id!!)
        }

        return loadTest.toLoadTestOutputDto(grafanaBaseUrl = grafanaProperties.baseUrl)
    }

    fun getLoadTestStatus(loadTestId: Long): LoadTestOutputDto {
        val loadTest = loadTestDbService.getLoadTestById(loadTestId)

        // Если тест выполнился и мы сохранили это, то нет смысла идти в сервис генерации нагрузки снова
        if (loadTest.status == LoadTestStatus.FINISHED ||
            loadTest.status == LoadTestStatus.FAILED ||
            loadTest.status == LoadTestStatus.STOPPED
        ) {
            return loadTest.toLoadTestOutputDto(grafanaBaseUrl = grafanaProperties.baseUrl)
        }

        val loadTestStatusDto = yandexTankApiFeignClient.getLoadTestStatus(loadTest.externalId!!)

        // Обновляем статус теста в базе
        loadTest.status = loadTestStatusDto.status.toLoadTestStatus()
        loadTest.stage = loadTestStatusDto.currentStage?.toLoadTestStage()

        // Обновляем дату финиша теста
        val instantNow = Instant.now()
        if (loadTest.finishDate == null &&
            (loadTest.status == LoadTestStatus.FINISHED ||
                    loadTest.status == LoadTestStatus.FAILED ||
                    loadTest.status == LoadTestStatus.STOPPED)
        ) {
            loadTest.finishDate = instantNow
        }

        loadTestDbService.saveLoadTest(loadTest)

        return loadTest.toLoadTestOutputDto(grafanaBaseUrl = grafanaProperties.baseUrl)
    }

    fun searchLoadTests(): List<LoadTestOutputDto> {
        return loadTestDbService
            .searchLoadTests()
            .map { getLoadTestStatus(it.id!!) }
    }

    fun searchLoadTests(nameFilter: String = "", status: LoadTestStatus?): List<LoadTestOutputDto> {
        return loadTestDbService
            .searchLoadTests(nameFilter, status)
            .map { getLoadTestStatus(it.id!!) }
    }

    fun getAllRunningLoadTests(): List<LoadTestOutputDto> {
        return searchLoadTests().filter { it.status == LoadTestStatus.RUNNING }
    }

    /**
     * Останавливаем тест в яндекс танке и обновляем статус в БД.
     */
    fun stopLoadTest(loadTestId: Long): LoadTestOutputDto {
        val loadTest = loadTestDbService.getLoadTestById(loadTestId)
        yandexTankApiFeignClient.stopLoadTest(loadTest.externalId!!)
        return getLoadTestStatus(loadTestId)
    }

    /**
     * @return true, если нужно проверить статус еще раз, иначе false
     */
    fun uploadFileAndStartLoadTest(loadTest: LoadTest) {
        logger.info("Start uploading ammo file and restart test ${loadTest.id}")
        // Проверяем, что статус у теста корректный
        val statusDto = yandexTankApiFeignClient.getLoadTestStatus(loadTestId = loadTest.externalId!!)
        if (statusDto.status == "failed") { // тест почему-то зафэйлился
            logger.warn("Test ${loadTest.id} is failed")
            try {
                yandexTankApiFeignClient.stopLoadTest(loadTestId = loadTest.externalId!!)
            } catch (e: SessionAlreadyStoppedException) {
                logger.info("Load test with id ${loadTest.id} is already stopped")
            }
            loadTest.status = LoadTestStatus.FAILED
            loadTestDbService.saveLoadTest(loadTest)
        }

        if (statusDto.status == "running" && statusDto.`break` == "finished") { // тест уже запущен
            logger.warn("Test ${loadTest.id} is running")
            loadTest.status = LoadTestStatus.RUNNING
            loadTestDbService.saveLoadTest(loadTest)
        }


        // Загружаем файл в текущую сессию
        val uploadFileResponseDto = yandexTankApiFeignClient.uploadFile(
            sessionId = loadTest.externalId!!,
            filename = loadTest.scenario.ammo?.name
                ?: throw YandexTankException("Ammo ${loadTest.scenario.ammo!!.id} does not have ammo file name"),
            fileContent = loadTest.scenario.ammo?.ammo
                ?: throw YandexTankException("Ammo ${loadTest.scenario.ammo!!.id} does not have ammo file content"),
        )
        logger.info("File is loaded: $uploadFileResponseDto")

        // Снова запускаем тест
        val startTestResponseDto = yandexTankApiFeignClient.startLockedLoadTest(
            sessionId = loadTest.externalId!!
        )
        logger.info("Test is started: $startTestResponseDto")
    }

    private fun String.toLoadTestStatus(): LoadTestStatus = when (this) {
        "running" -> LoadTestStatus.RUNNING
        "failed" -> LoadTestStatus.FAILED
        "success" -> LoadTestStatus.FINISHED
        else -> {
            logger.error("Unexpected test status: $this")
            LoadTestStatus.FAILED
        }
    }

    private fun String.toLoadTestStage(): LoadTestStage? = when (this) {
        "lock" -> LoadTestStage.LOCK
        "init" -> LoadTestStage.INIT
        "configure" -> LoadTestStage.CONFIGURE
        "prepare" -> LoadTestStage.PREPARE
        "start" -> LoadTestStage.START
        "poll" -> LoadTestStage.POLL
        "end" -> LoadTestStage.END
        "postprocess" -> LoadTestStage.POSTPROCESS
        "unlock" -> LoadTestStage.UNLOCK
        "finished" -> LoadTestStage.FINISHED
        else -> {
            logger.error("Unexpected test stage: $this")
            null
        }
    }
}