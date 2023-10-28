package edikgoose.loadgenerator.exception.decoder

import com.fasterxml.jackson.databind.ObjectMapper
import edikgoose.loadgenerator.exception.YandexTankException
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.http.HttpStatus

class YandexTankApiClientErrorDecoder(private val mapper: ObjectMapper) : ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        when (response.status()) {
            HttpStatus.SERVICE_UNAVAILABLE.value() -> {
                throw YandexTankException("Another session is already running")
            }

            else -> {
                response.body().asInputStream().use { responseBodyIs ->
                    val body: String =
                        mapper.readValue(responseBodyIs, String::class.java)
                    throw YandexTankException(body)
                }
            }
        }
    }
}