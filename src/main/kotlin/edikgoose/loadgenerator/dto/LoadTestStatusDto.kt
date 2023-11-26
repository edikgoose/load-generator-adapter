package edikgoose.loadgenerator.dto

import edikgoose.loadgenerator.enumeration.LoadTestStatus
import java.time.Instant

data class LoadTestStatusDto(
    val status: String,
    val stageCompleted: Boolean,
    val currentStage: String?,
    val test: String?,
    val failures: Array<LoadTestStatusFailures>
)

data class LoadTestStatusOutputDto(
    val id: Long,
    val title: String?,
    val session: String,
    val status: LoadTestStatus,
    val grafanaUrl: String,
    val createdDate: Instant
)




data class LoadTestStatusFailures(
    val reason: String,
    val stage: String
)