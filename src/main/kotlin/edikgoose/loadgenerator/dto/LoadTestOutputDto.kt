package edikgoose.loadgenerator.dto

import edikgoose.loadgenerator.enumeration.LoadTestStatus
import java.time.Instant

data class LoadTestOutputDto (
    val id: Long,
    val scenario: ScenarioOutputDto,
    val name: String,
    val status: LoadTestStatus,
    val dashboardUrl: String,
    val createdDate: Instant
)