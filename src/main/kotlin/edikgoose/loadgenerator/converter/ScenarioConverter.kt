package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.entity.Scenario

fun Scenario.toScenarioOutputDto() = ScenarioOutputDto(
    id = this.id!!,
    name = this.name!!,
    scenarioConfig = this.config!!,
    createdDate = this.createdDate!!,
)