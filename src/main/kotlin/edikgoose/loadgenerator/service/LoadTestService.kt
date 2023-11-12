package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.converter.convertToLoadTest
import edikgoose.loadgenerator.converter.convertToYandexTankConfig
import edikgoose.loadgenerator.dto.LoadTestParamsDto
import edikgoose.loadgenerator.dto.LoadTestStartInformationDto
import edikgoose.loadgenerator.dto.LoadTestStatusDto
import edikgoose.loadgenerator.dto.LoadTestStopResponseDto
import edikgoose.loadgenerator.enumeration.LoadGeneratorEngine
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.feign.YandexTankApiFeignClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoadTestService(
    val grafanaDashboardService: GrafanaDashboardService,
    val yandexTankApiFeignClient: YandexTankApiFeignClient,
    val loadTestDbService: LoadTestDbService
) {
    val logger: Logger = LoggerFactory.getLogger(LoadTestService::class.java)

    @Transactional
    fun runLoadTest(loadTestParamsDto: LoadTestParamsDto): LoadTestStartInformationDto {
        val loadTest = loadTestParamsDto.convertToLoadTest()
        loadTestDbService.saveLoadTest(loadTest)
        when (loadTestParamsDto.loadGeneratorEngine) {
            LoadGeneratorEngine.YANDEX_TANK -> {
                val config = loadTest.convertToYandexTankConfig()
                logger.info("Config for Yandex tank:\n$config")

                val loadTestStartInformationDto = yandexTankApiFeignClient.runLoadTest(config)
                val grafanaDashboardUrl = grafanaDashboardService.createDashboard(loadTest.id!!)

                loadTest.externalId = loadTestStartInformationDto.session
                loadTest.grafanaUrl = grafanaDashboardUrl

                loadTestDbService.saveLoadTest(loadTest)

                loadTestStartInformationDto.grafanaUrl = grafanaDashboardUrl
                loadTestStartInformationDto.status = LoadTestStatus.RUNNING

                return loadTestStartInformationDto
            }
        }
    }

    fun getLoadTestStatus(loadTestId: String): LoadTestStatusDto {
        return yandexTankApiFeignClient.getLoadTestStatus(loadTestId)
    }

    fun stopLoadTest(loadTestId: String): LoadTestStopResponseDto {
        return yandexTankApiFeignClient.stopLoadTest(loadTestId)
    }
}