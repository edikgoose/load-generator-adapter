package edikgoose.loadgenerator.dto

import edikgoose.loadgenerator.enumeration.LoadTestStage
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import java.net.URL
import java.time.Instant

data class LoadTestOutputDto (
    val id: Long,
    val externalId: String,
    val scenario: ScenarioOutputDto,
    val name: String,
    val status: LoadTestStatus,
    val stage: LoadTestStage?,
    val dashboardUrl: URL,
    val startDate: Instant,
    val finishDate: Instant?
)