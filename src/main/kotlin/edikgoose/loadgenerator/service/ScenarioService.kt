package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.dto.ScenarioInputDto
import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.entity.Scenario
import edikgoose.loadgenerator.exception.NotFoundException
import edikgoose.loadgenerator.repository.ScenarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ScenarioService(
    @Autowired val scenarioRepository: ScenarioRepository
) {
    fun createScenario(scenarioDto: ScenarioInputDto): ScenarioOutputDto =
        scenarioRepository.save(scenarioDto.toScenario()).toScenarioOutputDto()


    fun deleteScenario(id: Long): ScenarioOutputDto {
        val scenario: Scenario? = scenarioRepository.findById(id).orElse(null)
        return scenario?.let {
            scenarioRepository.deleteById(id)
            it.toScenarioOutputDto()
        } ?: throw NotFoundException(id, Scenario::class.java)
    }

    fun getScenario(id: Long): ScenarioOutputDto {
        val scenario: Scenario? = scenarioRepository.findById(id).orElse(null)
        return scenario?.toScenarioOutputDto() ?: throw NotFoundException(id, Scenario::class.java)
    }

    fun getAllScenarios(): List<ScenarioOutputDto> = scenarioRepository.findAll().map { it.toScenarioOutputDto() }

    fun ScenarioInputDto.toScenario() = Scenario(
        id = null,
        name = this.name,
        config = this.scenarioConfig,
        createdDate = null,
    )

    fun Scenario.toScenarioOutputDto() = ScenarioOutputDto(
        id = this.id!!,
        name = this.name!!,
        scenarioConfig = this.config!!,
        createdDate = this.createdDate!!,
    )
}