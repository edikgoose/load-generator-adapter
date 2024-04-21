package edikgoose.loadgenerator.dto

import java.time.Instant

data class ScenarioOutputDto (
    val id: Long,
    var name: String,
    var yandexTankConfig: String,
    val createdDate: Instant,
    var ammoId: Long?,
    var systemConfigurationDto: SystemConfigurationDto?,
)
