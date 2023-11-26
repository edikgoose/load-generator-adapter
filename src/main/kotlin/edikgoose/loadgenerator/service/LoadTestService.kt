package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.converter.convertToLoadTest
import edikgoose.loadgenerator.converter.convertToYandexTankConfig
import edikgoose.loadgenerator.converter.toLoadSchemaDto
import edikgoose.loadgenerator.converter.toLoadTestStatusOutputDto
import edikgoose.loadgenerator.dto.LoadTestParamsDto
import edikgoose.loadgenerator.dto.LoadTestStatusOutputDto
import edikgoose.loadgenerator.dto.LoadTestStopResponseDto
import edikgoose.loadgenerator.enumeration.LoadGeneratorEngine
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.feign.YandexTankApiFeignClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис для управления нагрузочными тестами
 */
@Service
class LoadTestService(
    val grafanaDashboardService: GrafanaDashboardService,
    val yandexTankApiFeignClient: YandexTankApiFeignClient,
    val loadTestDbService: LoadTestDbService
) {
    val logger: Logger = LoggerFactory.getLogger(LoadTestService::class.java)

    @Transactional
    fun runLoadTest(loadTestParamsDto: LoadTestParamsDto): LoadTestStatusOutputDto {
        val loadTest = loadTestParamsDto.convertToLoadTest()
        val loadScheme = loadTestParamsDto.loadScheme.toLoadSchemaDto()
        loadTestDbService.saveLoadTest(loadTest)
        when (loadTestParamsDto.loadGeneratorEngine) {
            LoadGeneratorEngine.YANDEX_TANK -> {
                val config = loadTest.convertToYandexTankConfig()
                logger.info("Config for Yandex tank:\n$config")

                val loadTestStartInformationDto = yandexTankApiFeignClient.runLoadTest(config)
                loadTestStartInformationDto.id = loadTest.id

                loadTest.externalId = loadTestStartInformationDto.session

                // Создаем дашборд для теста
                val grafanaDashboardUrl = grafanaDashboardService.createDashboard(loadTest, loadScheme.duration)

                loadTest.grafanaUrl = grafanaDashboardUrl
                loadTest.status = LoadTestStatus.RUNNING

                loadTestDbService.saveLoadTest(loadTest)

                logger.info("Load test has successfully started. External id: ${loadTest.externalId}")
                return loadTest.toLoadTestStatusOutputDto()
            }
        }
    }

    fun getLoadTestStatus(loadTestId: Long): LoadTestStatusOutputDto {
        val loadTest = loadTestDbService.getLoadTestById(loadTestId)

        // Если тест выполнился и мы сохранили это, то нет смысла идти в сервис генерации нагрузки снова
        if (loadTest.status == LoadTestStatus.FINISHED ||
            loadTest.status == LoadTestStatus.FAILED ||
            loadTest.status == LoadTestStatus.STOPPED
        ) {
            return loadTest.toLoadTestStatusOutputDto()
        }

        val loadTestStatusDto = yandexTankApiFeignClient.getLoadTestStatus(loadTest.externalId!!)

        // Обновляем статус теста в базе
        loadTest.status = loadTestStatusDto.status.toLoadTestStatus()
        loadTestDbService.saveLoadTest(loadTest)

        return loadTest.toLoadTestStatusOutputDto()
    }

    fun getAllLoadTests(): List<LoadTestStatusOutputDto> {
        return loadTestDbService
            .getAllLoadTests()
            .map { getLoadTestStatus(it.id!!) }
    }

    fun getAllRunningLoadTests(): List<LoadTestStatusOutputDto> {
        return getAllLoadTests().filter { it.status == LoadTestStatus.RUNNING }
    }

    fun stopLoadTest(loadTestId: Long): LoadTestStopResponseDto {
        val loadTest = loadTestDbService.getLoadTestById(loadTestId)

        return yandexTankApiFeignClient.stopLoadTest(loadTest.externalId!!).also {
            loadTest.status = LoadTestStatus.STOPPED
            loadTestDbService.saveLoadTest(loadTest)
        }
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
}