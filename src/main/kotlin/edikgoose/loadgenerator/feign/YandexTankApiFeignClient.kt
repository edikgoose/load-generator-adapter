package edikgoose.loadgenerator.feign

import edikgoose.loadgenerator.dto.YandexTankTestRunOutputDto
import edikgoose.loadgenerator.dto.LoadTestStatusDto
import edikgoose.loadgenerator.dto.LoadTestStopResponseDto
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

interface YandexTankApiFeignClient {
    @PostMapping(
        path = ["/run"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun runLoadTest(config: String): YandexTankTestRunOutputDto

    @GetMapping(path = ["/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLoadTestStatus(@RequestParam(name = "session") loadTestId: String): LoadTestStatusDto

    @GetMapping(
        path = ["stop"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun stopLoadTest(@RequestParam(name = "session") loadTestId: String): LoadTestStopResponseDto
}