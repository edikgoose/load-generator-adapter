package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.Ammo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AmmoRepository: JpaRepository<Ammo, Long> {
    fun findByName(name: String): Ammo?

    @Query("""
        SELECT am from Ammo am
        WHERE LOWER(am.name) like lower(concat('%', LOWER(:nameFilter), '%'))
    """)
    fun searchAmmo(nameFilter: String): List<Ammo>

}