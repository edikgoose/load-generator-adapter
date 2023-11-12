package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.GrafanaProperties
import edikgoose.loadgenerator.converter.YandexTankConfigConverter
import edikgoose.loadgenerator.dto.LoadTestParamsDto
import edikgoose.loadgenerator.dto.LoadTestStartInformationDto
import edikgoose.loadgenerator.dto.LoadTestStatusDto
import edikgoose.loadgenerator.dto.LoadTestStopResponseDto
import edikgoose.loadgenerator.feign.YandexTankApiClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class LoadTestService(
    val grafanaProperties: GrafanaProperties,
    val yandexTankConfigConverter: YandexTankConfigConverter,
    val yandexTankApiClient: YandexTankApiClient
) {
    val logger: Logger = LoggerFactory.getLogger(LoadTestService::class.java)

    fun runLoadTest(loadTestParamsDto: LoadTestParamsDto): LoadTestStartInformationDto {
        val id: String = UUID.randomUUID().toString()
        return startTest(loadTestParamsDto, id)
    }

    fun getLoadTestStatus(loadTestId: String): LoadTestStatusDto {
        return yandexTankApiClient.getLoadTestStatus(loadTestId)
    }

    fun stopLoadTest(loadTestId: String): LoadTestStopResponseDto {
        return yandexTankApiClient.stopLoadTest(loadTestId)
    }

    private fun startTest(loadTestParamsDto: LoadTestParamsDto, id: String): LoadTestStartInformationDto {
        val config = yandexTankConfigConverter.convert(loadTestParamsDto, id)
        logger.info("Config for Yandex tank:\n$config")
        return yandexTankApiClient.runLoadTest(config)
    }
}