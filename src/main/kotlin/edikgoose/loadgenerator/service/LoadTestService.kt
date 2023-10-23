package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.GrafanaProperties
import edikgoose.loadgenerator.configuration.YandexTankProperties
import edikgoose.loadgenerator.converter.YandexTankConfigConverter
import edikgoose.loadgenerator.dto.LoadTestParams
import edikgoose.loadgenerator.exception.RestTemplateServerException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

@Service
class LoadTestService(
    val yandexTankProperties: YandexTankProperties,
    val grafanaProperties: GrafanaProperties,
    val yandexTankConfigConverter: YandexTankConfigConverter,
) {
    val logger: Logger = LoggerFactory.getLogger(LoadTestService::class.java)
    val restTemplate: RestTemplate = RestTemplate()

    fun runLoadTest(loadTestParams: LoadTestParams): ResponseEntity<String> {
        val id: String = UUID.randomUUID().toString()
//        pushNewDashboard(id)
        return startTest(loadTestParams, id)
    }

    private fun pushNewDashboard(id: String) {
        val stream = this.javaClass
            .classLoader
            .getResourceAsStream("grafana/dashboard-template.json")
        val dashboardConfig = String(stream?.readAllBytes()!!, UTF_8)
            .apply {
                replace("overall", "${id}overall")
            }

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity(dashboardConfig, headers)
        val url = grafanaProperties.baseUrl

        val uri = URI.create("http://$url/api/dashboards/db")

        try {
            restTemplate.postForEntity(uri, request, String::class.java)
        } catch (e: RuntimeException) {
            logger.error("Error during request to grafana: ", e.cause)
            throw RestTemplateServerException(e.message!!, e.cause)
        }
    }

    private fun startTest(loadTestParams: LoadTestParams, id: String): ResponseEntity<String> {
        val config = yandexTankConfigConverter.convert(loadTestParams, id)
        logger.info("Config for Yandex tank: $config")
        val headers = HttpHeaders()
        headers.contentType = MediaType.TEXT_PLAIN

        val request = HttpEntity(config, headers)
        val url = yandexTankProperties.baseUrl

        val uri = URI.create("http://$url/run")

        try {
            return restTemplate.postForEntity(uri, request, String::class.java)
        } catch (e: RuntimeException) {
            logger.error("Error during request to yandex tank: ", e.cause)
            throw RestTemplateServerException(e.message!!, e.cause)
        }
    }

}