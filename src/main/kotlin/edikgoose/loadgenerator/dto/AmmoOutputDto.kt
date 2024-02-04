package edikgoose.loadgenerator.dto

import java.time.Instant

data class AmmoOutputDto (
    val id: Long,
    val name: String,
    val ammo: String,
    val createdDate: Instant
)