package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.SystemConfigurationDto
import edikgoose.loadgenerator.service.SystemConfigurationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/system-configuration")
@Tag(name = "API for operations with system configuration")
class SystemConfigurationController(
    private val systemConfigurationService: SystemConfigurationService
) {
    @PostMapping("/default")
    @Parameter(
        name = "name",
        `in` = ParameterIn.QUERY,
        schema = Schema(type = "string"),
        required = true,
        description = "Configuration name/name of service",
        example = "FoodAPI"
    )
    @Operation(summary = "Create configuration by accepting configuration itself")
    fun createConfig(
        @RequestParam(value = "name", required = true) name: String,
        @RequestBody config: String
    ): ResponseEntity<SystemConfigurationDto> {
        return ResponseEntity.ok(systemConfigurationService.createDefaultConfig(name, config))
    }

    @PostMapping("/consul")
    @Parameter(
        name = "name",
        `in` = ParameterIn.QUERY,
        schema = Schema(type = "string"),
        required = true,
        description = "Configuration name/name of service",
        example = "FoodAPI"
    )
    @Parameter(
        name = "key",
        `in` = ParameterIn.QUERY,
        schema = Schema(type = "string"),
        required = true,
        description = "Consul key",
        example = "/config/food-api/dev"
    )
    @Operation(summary = "Create configuration by accepting consul key")
    fun createConsulConfig(
        @RequestParam(value = "name", required = true) name: String,
        @RequestParam(value = "key", required = true) key: String,
    ): ResponseEntity<SystemConfigurationDto>  {
        return ResponseEntity.ok(systemConfigurationService.createConsulConfig(name, key))
    }

    @GetMapping("/{id}")
    @Parameter(
        name = "id",
        `in` = ParameterIn.PATH,
        schema = Schema(type = "integer", format = "int64"),
        required = true,
        description = "Configuration id",
        example = "1"
    )
    @Operation(summary = "Get configuration by id")
    fun getConfig(
        @PathVariable(value = "id") id: Long,
    ): ResponseEntity<SystemConfigurationDto> {
        return ResponseEntity.ok(systemConfigurationService.getConfig(id))
    }
}