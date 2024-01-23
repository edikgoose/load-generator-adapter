package edikgoose.loadgenerator.dto

import java.time.Instant

data class ScenarioOutputDto (
    val id: Long,
    val name: String,
    val scenarioConfig: String,
    val createdDate: Instant
)
