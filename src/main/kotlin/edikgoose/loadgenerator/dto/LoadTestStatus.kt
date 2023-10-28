package edikgoose.loadgenerator.dto

data class LoadTestStatus(
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