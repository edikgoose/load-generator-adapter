package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.Scenario
import org.springframework.data.jpa.repository.JpaRepository

interface ScenarioRepository: JpaRepository<Scenario, Long> 