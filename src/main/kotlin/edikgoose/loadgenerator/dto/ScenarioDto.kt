package edikgoose.loadgenerator.dto

import java.time.Instant


data class ScenarioInputDto (
    val name: String,
    val scenarioConfig: String
)

data class ScenarioOutputDto (
    val id: Long,
    val name: String,
    val scenarioConfig: String,
    val createdDate: Instant
)
