package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.LoadTestParams
import edikgoose.loadgenerator.dto.LoadTestStartInformation
import edikgoose.loadgenerator.dto.LoadTestStatus
import edikgoose.loadgenerator.service.LoadTestService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotBlank

@RestController
@Tag(name = "API для управления нагрузочными тестами", description = "API для управления нагрузочными тестами")
class LoadTestController(val loadTestService: LoadTestService) {
    @PostMapping("run")
    fun runLoadTest(loadTestParams: LoadTestParams): LoadTestStartInformation {
        return loadTestService.runLoadTest(loadTestParams)
    }

    @GetMapping("status")
    @Parameter(
        name = "testId",
        schema = Schema(type = "string"),
        description = "ID нагрузочного тестирования",
        required = true
    )
    fun stopLoadTest(@NotBlank @RequestParam("testId") session: String): LoadTestStatus {
        return loadTestService.stopLoadTest(session)
    }
}