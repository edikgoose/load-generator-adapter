package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.LoadTestParams
import edikgoose.loadgenerator.service.LoadTestService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "API для управления нагрузочными тестами", description = "API для управления нагрузочными тестами")
class LoadTestController(val loadTestService: LoadTestService) {
    @PostMapping("run")
    fun runLoadTest(loadTestParams: LoadTestParams): ResponseEntity<String> {
        return loadTestService.runLoadTest(loadTestParams)
    }
}