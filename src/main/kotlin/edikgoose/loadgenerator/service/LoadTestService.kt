package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.YandexTankProperties
import edikgoose.loadgenerator.converter.YandexTankConfigConverter
import edikgoose.loadgenerator.dto.LoadTestParams
import edikgoose.loadgenerator.exception.YandexTankServerException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class LoadTestService(
    val yandexTankProperties: YandexTankProperties,
    val yandexTankConfigConverter: YandexTankConfigConverter,
) {
    val logger: Logger = LoggerFactory.getLogger(LoadTestService::class.java)
    val restTemplate: RestTemplate = RestTemplate()

    fun runLoadTest(loadTestParams: LoadTestParams): ResponseEntity<String> {
        val config = yandexTankConfigConverter.convert(loadTestParams)

        val headers = HttpHeaders()
        headers.contentType = MediaType.TEXT_PLAIN

        val request = HttpEntity(config, headers)
        val url = yandexTankProperties.baseUrl

        val uri = URI.create("http://$url/validate")

        try {
        return restTemplate.postForEntity(uri, request, String::class.java)
        } catch (e: RuntimeException) {
            logger.error("Error during request to yandex tank: ", e.cause)
            throw YandexTankServerException(e.message!!, e.cause)
        }
    }

}