package edikgoose.loadgenerator.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason = "Error with yandex tank")
class RestTemplateServerException(override val message: String, override val cause: Throwable?): RuntimeException(message, cause)
