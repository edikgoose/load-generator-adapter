package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.AmmoOutputDto
import edikgoose.loadgenerator.entity.Ammo

fun Ammo.toAmmoOutputDto() =
    AmmoOutputDto(
        id = this.id!!,
        name = this.name!!,
        ammo = this.ammo!!,
        createdDate = this.createdDate!!
    )
