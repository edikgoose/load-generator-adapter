package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.*
import edikgoose.loadgenerator.service.LoadTestService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotBlank

@RestController
@Tag(name = "API для управления нагрузочными тестами", description = "API для управления нагрузочными тестами")
class LoadTestController(val loadTestService: LoadTestService) {
    @PostMapping("run")
    @Operation(summary = "Метод для запуска нагрузочного тестирования")
    fun runLoadTest(loadTestParamsDto: LoadTestParamsDto): ResponseEntity<LoadTestStatusOutputDto> {
        return ResponseEntity(loadTestService.runLoadTest(loadTestParamsDto), HttpStatus.OK)
    }

    @GetMapping("status/{testId}")
    @Operation(summary = "Метод для получение статуса теста")
    @Parameter(
        name = "testId",
        `in` = ParameterIn.PATH,
        schema = Schema(type = "integer", format = "int64"),
        description = "ID нагрузочного тестирования",
        required = true
    )
    fun getLoadTestStatus(@PathVariable @NotBlank testId: Long): ResponseEntity<LoadTestStatusOutputDto> {
        return ResponseEntity(loadTestService.getLoadTestStatus(testId), HttpStatus.OK)
    }

    @GetMapping("/status")
    @Operation(summary = "Метод для получение статуса всех доступных тестов")
    fun getAllLoadTests(): ResponseEntity<List<LoadTestStatusOutputDto>> {
        return ResponseEntity(loadTestService.getAllLoadTests(), HttpStatus.OK)
    }

    @GetMapping("/status-running")
    @Operation(summary = "Метод для получение статуса теста, которые исполняется в данный момент", description = "Для яндекс танка недоступен параллельный запуск тестов")
    fun getAllRunningLoadTests(): ResponseEntity<List<LoadTestStatusOutputDto>> {
        return ResponseEntity(loadTestService.getAllRunningLoadTests(), HttpStatus.OK)
    }

    @PutMapping("stop")
    @Parameter(
        name = "testId",
        schema = Schema(type = "integer", format = "int64"),
        description = "ID нагрузочного тестирования",
        required = true
    )
    @Operation(summary = "Метод для остановки теста")
    fun stopLoadTestStatus(@NotBlank @RequestParam("testId") testId: Long): ResponseEntity<LoadTestStopResponseDto> {
        return ResponseEntity(loadTestService.stopLoadTest(testId), HttpStatus.OK)
    }
}