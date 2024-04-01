package edikgoose.loadgenerator.exception.handler

import edikgoose.loadgenerator.controller.AmmoController
import edikgoose.loadgenerator.controller.LoadTestController
import edikgoose.loadgenerator.controller.ScenarioController
import edikgoose.loadgenerator.controller.SystemConfigurationController
import edikgoose.loadgenerator.exception.AmmoNameAlreadyExistsException
import edikgoose.loadgenerator.exception.AnotherSessionIsRunningException
import edikgoose.loadgenerator.exception.GrafanaDashboardPushException
import edikgoose.loadgenerator.exception.IllegalConfigException
import edikgoose.loadgenerator.exception.NotFoundException
import edikgoose.loadgenerator.exception.SessionAlreadyStoppedException
import edikgoose.loadgenerator.exception.SessionNotFoundException
import edikgoose.loadgenerator.exception.YandexTankException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.io.FileNotFoundException

@ControllerAdvice(assignableTypes = [LoadTestController::class, ScenarioController::class, AmmoController::class, SystemConfigurationController::class])
class LoadTestAdapterHandler {
    @ExceptionHandler(value = [YandexTankException::class])
    protected fun handleYandexTankException(ex: YandexTankException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ResponseEntity<String>(ex.message, HttpStatus.SERVICE_UNAVAILABLE))
    }

    @ExceptionHandler(value = [AnotherSessionIsRunningException::class])
    protected fun handleNotFoundException(ex: AnotherSessionIsRunningException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseEntity<String>(ex.message, HttpStatus.BAD_REQUEST))
    }

    @ExceptionHandler(value = [SessionAlreadyStoppedException::class])
    protected fun handleSessionAlreadyStoppedException(ex: SessionAlreadyStoppedException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ResponseEntity<String>(ex.message, HttpStatus.CONFLICT))
    }

    @ExceptionHandler(value = [SessionNotFoundException::class])
    protected fun handleSessionNotFoundException(ex: SessionNotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResponseEntity<String>(ex.message, HttpStatus.NOT_FOUND))
    }

    @ExceptionHandler(value = [GrafanaDashboardPushException::class])
    protected fun handleGrafanaDashboardPushException(ex: GrafanaDashboardPushException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ResponseEntity<String>(ex.message, HttpStatus.SERVICE_UNAVAILABLE))
    }


    @ExceptionHandler(value = [NotFoundException::class])
    protected fun handleNotFoundException(ex: NotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResponseEntity<String>(ex.message, HttpStatus.NOT_FOUND))
    }

    @ExceptionHandler(value = [FileNotFoundException::class])
    protected fun handleFileNotFoundException(ex: FileNotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResponseEntity<String>(ex.message, HttpStatus.NOT_FOUND))
    }

    @ExceptionHandler(value = [AmmoNameAlreadyExistsException::class, IllegalConfigException::class])
    protected fun handleBadRequestExceptions(ex: RuntimeException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseEntity<String>(ex.message, HttpStatus.BAD_REQUEST))
    }
}