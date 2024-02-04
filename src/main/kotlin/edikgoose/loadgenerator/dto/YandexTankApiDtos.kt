package edikgoose.loadgenerator.dto


data class LoadTestStatusDto(
    val status: String,
    val stageCompleted: Boolean,
    val currentStage: String?,
    val test: String?,
    val `break`: String?,
    val failures: Array<LoadTestStatusFailures>
)


data class LoadTestStatusFailures(
    val reason: String,
    val stage: String
)

data class LoadTestResponseDto (
    val reason: String
)

data class YandexTankTestRunOutputDto(
    val session: String,
)