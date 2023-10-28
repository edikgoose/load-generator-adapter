package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.GrafanaProperties
import edikgoose.loadgenerator.converter.YandexTankConfigConverter
import edikgoose.loadgenerator.dto.LoadTestParams
import edikgoose.loadgenerator.dto.LoadTestStartInformation
import edikgoose.loadgenerator.dto.LoadTestStatus
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

    fun runLoadTest(loadTestParams: LoadTestParams): LoadTestStartInformation {
        val id: String = UUID.randomUUID().toString()
        return startTest(loadTestParams, id)
    }

    fun stopLoadTest(loadTestId: String): LoadTestStatus {
        return yandexTankApiClient.getLoadTestStatus(loadTestId)
    }

    private fun startTest(loadTestParams: LoadTestParams, id: String): LoadTestStartInformation {
        val config = yandexTankConfigConverter.convert(loadTestParams, id)
        logger.info("Config for Yandex tank:\n$config")
        return yandexTankApiClient.runLoadTest(config)
    }

}