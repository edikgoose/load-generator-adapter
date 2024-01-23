package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.converter.toLoadTestOutputDto
import edikgoose.loadgenerator.dto.LoadTestOutputDto
import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.entity.Scenario
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.exception.NotFoundException
import edikgoose.loadgenerator.exception.YandexTankException
import edikgoose.loadgenerator.feign.YandexTankApiFeignClient
import edikgoose.loadgenerator.repository.ScenarioRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Duration.Companion.seconds

/**
 * Сервис для управления нагрузочными тестами
 */
@Service
class LoadTestService(
    val grafanaDashboardService: GrafanaDashboardService,
    val yandexTankApiFeignClient: YandexTankApiFeignClient,
    val loadTestDbService: LoadTestDbService,
    val scenarioRepository: ScenarioRepository
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
                dashboardUrl = null,
                createdDate = null,
            )
        )

        logger.info("Config for Yandex tank:\n${scenario.config}")
        val yandexTankTestRunOutputDto = yandexTankApiFeignClient.runLoadTest(scenario.config!!)

        loadTest.externalId = yandexTankTestRunOutputDto.session

        val duration: Long = extractDuration(config = scenario.config!!)
        // Создаем дашборд для теста
        val grafanaDashboardUrl = grafanaDashboardService.createDashboard(loadTest, duration.seconds)

        loadTest.dashboardUrl = grafanaDashboardUrl
        loadTest.status = LoadTestStatus.CREATED

        loadTestDbService.saveLoadTest(loadTest)
        logger.info("Load test has successfully started. External id: ${loadTest.externalId}")
        return loadTest.toLoadTestOutputDto()
    }

    fun getLoadTestStatus(loadTestId: Long): LoadTestOutputDto {
        val loadTest = loadTestDbService.getLoadTestById(loadTestId)

        // Если тест выполнился и мы сохранили это, то нет смысла идти в сервис генерации нагрузки снова
        if (loadTest.status == LoadTestStatus.FINISHED ||
            loadTest.status == LoadTestStatus.FAILED ||
            loadTest.status == LoadTestStatus.STOPPED
        ) {
            return loadTest.toLoadTestOutputDto()
        }

        val loadTestStatusDto = yandexTankApiFeignClient.getLoadTestStatus(loadTest.externalId!!)

        // Обновляем статус теста в базе
        loadTest.status = loadTestStatusDto.status.toLoadTestStatus()
        loadTestDbService.saveLoadTest(loadTest)

        return loadTest.toLoadTestOutputDto()
    }

    fun getAllLoadTests(): List<LoadTestOutputDto> {
        return loadTestDbService
            .getAllLoadTests()
            .map { getLoadTestStatus(it.id!!) }
    }

    fun getAllRunningLoadTests(): List<LoadTestOutputDto> {
        return getAllLoadTests().filter { it.status == LoadTestStatus.RUNNING }
    }

    /**
     * Останавливаем тест в яндекс танке и обновляем статус в БД.
     */
    fun stopLoadTest(loadTestId: Long): LoadTestOutputDto {
        val loadTest = loadTestDbService.getLoadTestById(loadTestId)
        yandexTankApiFeignClient.stopLoadTest(loadTest.externalId!!)

        loadTest.status = LoadTestStatus.STOPPED
        loadTestDbService.saveLoadTest(loadTest)

        return loadTest.toLoadTestOutputDto()
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

    /**
     * Parse yandex tank config and takes duration of the test in seconds
     * @return duration of test in seconds
     */
    private fun extractDuration(config: String): Long {
        for (line in config.lines()) {
            if (line.contains("schedule", ignoreCase = true)) {
                val numbers = line
                    .replace("schedule:", "")
                    .replace(")", "")
                    .replace("(", "")
                    .split(",")
                    .map { it.trim() }

                return parseDurationFromScheduleParam(numbers.last())
            }
        }
        throw YandexTankException("Config does not contains 'schedule' param")
    }

    /**
     * Parse from format 1m, 5s, 150ms
     */
    private fun parseDurationFromScheduleParam(durationStr: String): Long {
        if (durationStr.contains("m")) {
            return durationStr.replace("m", "").toLong() * 60
        } else if (durationStr.contains("s")) {
            return durationStr.replace("s", "").toLong()
        } else if (durationStr.contains("ms") || durationStr.toLongOrNull() == null) { // by default yandex tank use ms
            return durationStr.replace("ms", "").toLong() / 60
        }

        throw YandexTankException("Illegal unit of time: $durationStr")
    }
}