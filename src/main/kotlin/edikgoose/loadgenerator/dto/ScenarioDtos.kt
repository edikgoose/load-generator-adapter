package edikgoose.loadgenerator.dto

import java.time.Instant

data class ScenarioOutputDto (
    val id: Long,
    val name: String,
    val yandexTankConfig: String,
    val createdDate: Instant,
    val ammoId: Long?,
    val systemConfigurationDto: SystemConfigurationDto?,
)
