package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.entity.Scenario

fun Scenario.toScenarioOutputDto() = ScenarioOutputDto(
    id = this.id!!,
    name = this.name!!,
    yandexTankConfig = this.yandexTankConfig!!,
    createdDate = this.createdDate!!,
    ammoId = this.ammo?.id,
    systemConfigurationDto = this.systemConfiguration?.toDto(),
)