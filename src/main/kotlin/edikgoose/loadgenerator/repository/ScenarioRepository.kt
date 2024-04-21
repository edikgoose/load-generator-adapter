package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.Scenario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ScenarioRepository: JpaRepository<Scenario, Long> {
    @Query("""
        SELECT sc from Scenario sc
        WHERE sc.name like lower(concat('%', :nameFilter, '%'))
    """)
    fun searchScenarios(nameFilter: String): List<Scenario>
}