package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.LoadTestParams
import edikgoose.loadgenerator.service.LoadTestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoadTestController(val loadTestService: LoadTestService) {
    @PostMapping("run")
    fun runLoadTest(loadTestParams: LoadTestParams): ResponseEntity<String> {
        return loadTestService.runLoadTest(loadTestParams)
    }
}