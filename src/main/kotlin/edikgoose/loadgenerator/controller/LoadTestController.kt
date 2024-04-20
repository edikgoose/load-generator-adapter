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
import jakarta.validation.constraints.NotBlank

@RestController
@Tag(name = "API для управления нагрузочными тестами", description = "API для управления нагрузочными тестами")
class LoadTestController(val loadTestService: LoadTestService) {
    @PostMapping(BASE_URL)
    @Parameter(
        name = "scenarioId",
        `in` = ParameterIn.QUERY,
        schema = Schema(type = "integer", format = "int64"),
        required = true,
        description = "ID сценария нагрузки",
        example = "1"
    )
    @Parameter(
        name = "name",
        `in` = ParameterIn.QUERY,
        schema = Schema(type = "string"),
        required = true,
        description = "Название теста",
        example = "Провер \n" +
                "KafkaKaasC\n" +
                "\u200Eonfig.java\u200Eка после включения тогла"
    )
    @Operation(summary = "Метод для запуска нагрузочного тестирования")
    fun runLoadTest(
        @RequestParam(value = "scenarioId", required = true) scenarioId: Long,
        @RequestParam(value = "name", required = true) name: String,
    ): ResponseEntity<LoadTestOutputDto> {
        return ResponseEntity(loadTestService.runLoadTest(scenarioId, name), HttpStatus.OK)
    }

    @GetMapping("${BASE_URL}/{testId}")
    @Operation(summary = "Метод для получение статуса теста")
    @Parameter(
        name = "testId",
        `in` = ParameterIn.PATH,
        schema = Schema(type = "integer", format = "int64"),
        description = "ID нагрузочного тестирования",
        required = true
    )
    fun getLoadTestStatus(@PathVariable @NotBlank testId: Long): ResponseEntity<LoadTestOutputDto> {
        return ResponseEntity(loadTestService.getLoadTestStatus(testId), HttpStatus.OK)
    }

    @GetMapping(BASE_URL)
    @Operation(summary = "Метод для получение статуса всех доступных тестов")
    fun getAllLoadTests(): ResponseEntity<List<LoadTestOutputDto>> {
        return ResponseEntity(loadTestService.searchLoadTests(), HttpStatus.OK)
    }

    @GetMapping("${BASE_URL}/running")
    @Operation(
        summary = "Метод для получение статуса теста, которые исполняется в данный момент",
        description = "Для яндекс танка недоступен параллельный запуск тестов"
    )
    fun getAllRunningLoadTests(): ResponseEntity<List<LoadTestOutputDto>> {
        return ResponseEntity(loadTestService.getAllRunningLoadTests(), HttpStatus.OK)
    }

    @PutMapping("${BASE_URL}/stop")
    @Parameter(
        name = "testId",
        schema = Schema(type = "integer", format = "int64"),
        description = "ID нагрузочного тестирования",
        required = true
    )
    @Operation(summary = "Метод для остановки теста")
    fun stopLoadTestStatus(@NotBlank @RequestParam("testId") testId: Long): ResponseEntity<LoadTestOutputDto> {
        return ResponseEntity(loadTestService.stopLoadTest(testId), HttpStatus.OK)
    }

    companion object {
        const val BASE_URL = "api/load-test"
    }
}