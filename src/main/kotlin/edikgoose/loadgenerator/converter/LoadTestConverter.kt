package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.LoadTestOutputDto
import edikgoose.loadgenerator.entity.LoadTest

fun LoadTest.toLoadTestOutputDto(): LoadTestOutputDto = LoadTestOutputDto(
    id = id!!,
    scenario = scenario.toScenarioOutputDto(),
    name = name!!,
    status = status,
    dashboardUrl = dashboardUrl!!,
    createdDate = createdDate!!,
)