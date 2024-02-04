package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.Ammo
import org.springframework.data.jpa.repository.JpaRepository

interface AmmoRepository: JpaRepository<Ammo, Long> {
    fun findByName(name: String): Ammo?
}