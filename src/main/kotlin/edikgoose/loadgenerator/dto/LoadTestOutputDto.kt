package edikgoose.loadgenerator.dto

import edikgoose.loadgenerator.enumeration.LoadTestStage
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

fun Instant.toUiFormat(): String {
    val zoneId = ZoneId.systemDefault()
    val dateTime = LocalDateTime.ofInstant(this, zoneId)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    return dateTime.format(formatter)
}