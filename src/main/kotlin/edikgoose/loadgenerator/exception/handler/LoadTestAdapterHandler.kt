package edikgoose.loadgenerator.exception.handler

import edikgoose.loadgenerator.controller.LoadTestController
import edikgoose.loadgenerator.exception.YandexTankException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice(assignableTypes = [LoadTestController::class])
class LoadTestAdapterHandler {
    @ExceptionHandler(value = [YandexTankException::class])
    protected fun handleNotFoundException(ex: YandexTankException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ex.message)
    }
}