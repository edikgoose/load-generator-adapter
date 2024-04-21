package edikgoose.loadgenerator.dto

import java.time.Instant

data class AmmoOutputDto (
    var id: Long,
    var name: String,
    var ammo: String,
    var createdDate: Instant
)