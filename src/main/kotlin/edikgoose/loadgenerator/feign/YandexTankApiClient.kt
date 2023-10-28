package edikgoose.loadgenerator.feign

import edikgoose.loadgenerator.dto.LoadTestStartInformation
import edikgoose.loadgenerator.dto.LoadTestStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

interface YandexTankApiClient {
    @PostMapping(
        path = ["/run"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun runLoadTest(config: String): LoadTestStartInformation

    @GetMapping(path = ["/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLoadTestStatus(@RequestParam(name = "session") loadTestId: String): LoadTestStatus
}