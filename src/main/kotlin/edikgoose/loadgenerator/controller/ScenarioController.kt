package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.service.ScenarioService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotBlank

@RestController
@Tag(name = "API для управления сценариями тестов", description = "API для управления сценариями тестов")
class ScenarioController(
    @Autowired val scenarioService: ScenarioService
) {
    @PostMapping(BASE_URL)
    @Parameter(
        name = "name",
        `in` = ParameterIn.QUERY,
        schema = Schema(type = "string"),
        required = true,
        description = "Название сценария",
        example = "Сценарий до 500 рпс"
    )
    @Operation(summary = "Метод для создания сценария нагрузки")
    fun createScenario(
        @RequestParam(value = "name", required = true) name: String,
        @RequestBody config: String
    ): ResponseEntity<ScenarioOutputDto> =
        ResponseEntity(scenarioService.createScenario(name, config), HttpStatus.OK)

    @DeleteMapping("$BASE_URL/{id}")
    @Operation(summary = "Метод для удаления сценария нагрузки")
    @Parameter(
        name = "id",
        `in` = ParameterIn.PATH,
        schema = Schema(type = "integer", format = "int64"),
        description = "ID сценария",
        required = true
    )
    fun deleteScenario(
        @PathVariable @NotBlank id: Long,
    ): ResponseEntity<ScenarioOutputDto> =
        ResponseEntity(scenarioService.deleteScenario(id), HttpStatus.OK)

    @GetMapping("$BASE_URL/{id}")
    @Operation(summary = "Метод для получения сценария нагрузки")
    @Parameter(
        name = "id",
        `in` = ParameterIn.PATH,
        schema = Schema(type = "integer", format = "int64"),
        description = "ID сценария",
        required = true
    )
    fun getScenario(@PathVariable @NotBlank id: Long): ResponseEntity<ScenarioOutputDto> =
        ResponseEntity(scenarioService.getScenario(id), HttpStatus.OK)

    @GetMapping(BASE_URL)
    @Operation(summary = "Метод для получения всех сценариев нагрузки")
    fun getAllScenarios(): ResponseEntity<List<ScenarioOutputDto>> =
        ResponseEntity(scenarioService.getAllScenarios(), HttpStatus.OK)

    companion object {
        const val BASE_URL = "api/scenario"
    }
}