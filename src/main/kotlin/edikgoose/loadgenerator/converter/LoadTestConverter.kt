package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.LoadTestOutputDto
import edikgoose.loadgenerator.entity.LoadTest
import java.net.URL
import java.time.Instant

fun LoadTest.toLoadTestOutputDto(grafanaBaseUrl: String): LoadTestOutputDto = LoadTestOutputDto(
    id = id!!,
    externalId = externalId ?: "error",
    scenario = scenario.toScenarioOutputDto(),
    name = name!!,
    status = status,
    stage = stage,
    dashboardUrl = this.generateDashboardUrl(grafanaBaseUrl = grafanaBaseUrl),
    startDate = createdDate!!,
    finishDate = finishDate,
)

fun LoadTest.generateDashboardUrl(grafanaBaseUrl: String): URL {
    val urlStart = "$grafanaBaseUrl/d/yandex-tank/results-of-load-tests?orgId=1&"
    val fromDate = generateUrlParam("from", toUnixTimestamp(this.createdDate ?: Instant.now()))
    val testIdVar = generateUrlParam("var-test_id", this.id.toString())
    val toDate = generateUrlParam("to", if (this.finishDate == null) "now" else toUnixTimestamp(this.finishDate!!))

    return URL(
        "$urlStart$testIdVar&$fromDate&$toDate" +
                if (this.finishDate == null) "" else "&${
                    generateUrlParam(
                        "refresh",
                        "1s"
                    )
                }"
    )
}

private fun generateUrlParam(name: String, value: String) = "$name=$value"

private fun toUnixTimestamp(date: Instant) = "${date.epochSecond}000"
