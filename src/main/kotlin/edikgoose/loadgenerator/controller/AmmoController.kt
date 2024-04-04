package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.AmmoOutputDto
import edikgoose.loadgenerator.service.AmmoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.constraints.NotBlank

@RestController
@Tag(name = "API для управления паронами", description = "API для управления патронами")
class AmmoController(
    @Autowired private val ammoService: AmmoService
) {
    @PostMapping(BASE_URL)
    @Parameter(
        name = "name",
        `in` = ParameterIn.QUERY,
        schema = Schema(type = "string"),
        required = true,
        description = "Название файла для патронов",
        example = "ammo.txt"
    )
    @Operation(summary = "Метод для создания патронов")
    fun createAmmo(
        @RequestParam(value = "name", required = true) name: String,
        @RequestBody ammo: String
    ): ResponseEntity<AmmoOutputDto> =
        ResponseEntity(ammoService.createAmmo(name, ammo), HttpStatus.OK)

    @GetMapping("$BASE_URL/{id}")
    @Operation(summary = "Метод для получения патронов")
    @Parameter(
        name = "id",
        `in` = ParameterIn.PATH,
        schema = Schema(type = "integer", format = "int64"),
        description = "ID патрона",
        required = true
    )
    fun getAmmo(@PathVariable @NotBlank id: Long): ResponseEntity<AmmoOutputDto> =
        ResponseEntity(ammoService.getAmmo(id), HttpStatus.OK)

    @GetMapping(BASE_URL)
    @Operation(summary = "Метод для получения всех патронов")
    fun getAllAmmo(): ResponseEntity<List<AmmoOutputDto>> =
        ResponseEntity(ammoService.getAllAmmo(), HttpStatus.OK)

    companion object {
        const val BASE_URL = "api/ammo"
    }
}