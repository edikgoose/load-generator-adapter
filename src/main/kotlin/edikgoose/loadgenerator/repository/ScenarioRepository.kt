package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.Scenario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ScenarioRepository: JpaRepository<Scenario, Long> {
    @Query("""
        SELECT sc from Scenario sc
        WHERE LOWER(sc.name) like lower(concat('%', LOWER(:nameFilter), '%'))
    """)
    fun searchScenarios(nameFilter: String): List<Scenario>
}