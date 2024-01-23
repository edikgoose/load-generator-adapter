package edikgoose.loadgenerator.dto


data class LoadTestStatusDto(
    val status: String,
    val stageCompleted: Boolean,
    val currentStage: String?,
    val test: String?,
    val failures: Array<LoadTestStatusFailures>
)


data class LoadTestStatusFailures(
    val reason: String,
    val stage: String
)

data class LoadTestStopResponseDto (
    val reason: String
)

data class YandexTankTestRunOutputDto(
    val session: String,
)