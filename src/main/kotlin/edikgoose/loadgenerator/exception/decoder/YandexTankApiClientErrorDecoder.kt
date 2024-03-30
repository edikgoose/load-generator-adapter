package edikgoose.loadgenerator.exception.decoder

import com.fasterxml.jackson.databind.ObjectMapper
import edikgoose.loadgenerator.exception.SessionAlreadyStoppedException
import edikgoose.loadgenerator.exception.AnotherSessionIsRunningException
import edikgoose.loadgenerator.exception.SessionNotFoundException
import edikgoose.loadgenerator.exception.YandexTankException
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.http.HttpStatus

class YandexTankApiClientErrorDecoder(private val mapper: ObjectMapper) : ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        when (response.status()) {
            HttpStatus.CONFLICT.value() -> {
                throw SessionAlreadyStoppedException("This session is already stopped.")
            }
            HttpStatus.NOT_FOUND.value() -> {
                throw SessionNotFoundException("Yandex.Tank doesn't have session with this ID")
            }
            HttpStatus.SERVICE_UNAVAILABLE.value() -> {
                throw AnotherSessionIsRunningException("Another session is already running")
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