package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.LoadTestParamsDto
import edikgoose.loadgenerator.dto.LoadTestStartInformationDto
import edikgoose.loadgenerator.dto.LoadTestStatusDto
import edikgoose.loadgenerator.dto.LoadTestStopResponseDto
import edikgoose.loadgenerator.service.LoadTestService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotBlank

@RestController
@Tag(name = "API для управления нагрузочными тестами", description = "API для управления нагрузочными тестами")
class LoadTestController(val loadTestService: LoadTestService) {
    @PostMapping("run-linear-load")
    fun runLoadTest(loadTestParamsDto: LoadTestParamsDto): ResponseEntity<LoadTestStartInformationDto> {
        return ResponseEntity(loadTestService.runLoadTest(loadTestParamsDto), HttpStatus.OK)
    }

    @GetMapping("status")
    @Parameter(
        name = "testId",
        schema = Schema(type = "string"),
        description = "ID нагрузочного тестирования",
        required = true
    )
    fun getLoadTestStatus(@NotBlank @RequestParam("testId") session: String): ResponseEntity<LoadTestStatusDto> {
        return ResponseEntity(loadTestService.getLoadTestStatus(session), HttpStatus.OK)
    }

    @PutMapping("stop")
    @Parameter(
        name = "testId",
        schema = Schema(type = "string"),
        description = "ID нагрузочного тестирования",
        required = true
    )
    fun stopLoadTestStatus(@NotBlank @RequestParam("testId") session: String): ResponseEntity<LoadTestStopResponseDto> {
        return ResponseEntity(loadTestService.stopLoadTest(session), HttpStatus.OK)
    }
}