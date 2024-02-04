package edikgoose.loadgenerator.feign

import edikgoose.loadgenerator.dto.LoadTestStatusDto
import edikgoose.loadgenerator.dto.LoadTestResponseDto
import edikgoose.loadgenerator.dto.YandexTankTestRunOutputDto
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
    fun runLoadTest(
        config: String,
        @RequestParam(name = "break") breakStage: String = "finished"
    ): YandexTankTestRunOutputDto

    @GetMapping(path = ["/status"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLoadTestStatus(@RequestParam(name = "session") loadTestId: String): LoadTestStatusDto

    @GetMapping(
        path = ["stop"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun stopLoadTest(@RequestParam(name = "session") loadTestId: String): LoadTestResponseDto

    @PostMapping(
        path = ["upload"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun uploadFile(
        @RequestParam(name = "session") sessionId: String,
        @RequestParam(name = "filename") filename: String,
        fileContent: String
    ): LoadTestResponseDto

    @GetMapping(
        path = ["/run"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun startLockedLoadTest(
        @RequestParam(name = "session") sessionId: String,
    ): LoadTestResponseDto
}