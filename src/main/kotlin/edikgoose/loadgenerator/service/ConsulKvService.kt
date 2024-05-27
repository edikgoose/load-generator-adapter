package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.ConsulProperties
import edikgoose.loadgenerator.log
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate


/**
 * Service for get and update KV store of Consul
 */
@Service
class ConsulKvService(
    private val consulProperties: ConsulProperties,
    restTemplateBuilder: RestTemplateBuilder,
) {
    private val restTemplate: RestTemplate =
        restTemplateBuilder
            .errorHandler(RestTemplateResponseErrorHandler())
            .build()

    fun getValue(key: String): String? {
        return restTemplate
            .getForEntity(
                "${consulProperties.url}/v1/kv/$key?raw=true",
                String::class.java
            ).body
    }

    fun putValue(key: String, value: String): Boolean {
        val request: HttpEntity<String> = HttpEntity(value)
        return restTemplate.exchange("${consulProperties.url}/v1/kv/$key", HttpMethod.PUT, request, Boolean::class.java).body
            ?: false
    }

    inner class RestTemplateResponseErrorHandler : ResponseErrorHandler {
        override fun hasError(httpResponse: ClientHttpResponse): Boolean {
            return httpResponse.statusCode.is5xxServerError ||
                    httpResponse.statusCode.is4xxClientError
        }

        override fun handleError(httpResponse: ClientHttpResponse) {
            if (httpResponse.statusCode.is5xxServerError) {
                //Handle SERVER_ERROR
                throw HttpClientErrorException(httpResponse.statusCode)
            } else if (httpResponse.statusCode.is4xxClientError) {
                //Handle CLIENT_ERROR
                if (httpResponse.getStatusCode() === HttpStatus.NOT_FOUND) {
                    log.error("Key is not found in consul: ${String(httpResponse.body.readAllBytes())}")
                }
                throw HttpClientErrorException(httpResponse.statusCode)
            }
        }
    }
}
