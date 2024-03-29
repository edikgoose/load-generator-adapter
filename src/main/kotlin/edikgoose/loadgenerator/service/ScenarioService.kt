package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.converter.toScenarioOutputDto
import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.entity.Ammo
import edikgoose.loadgenerator.entity.Scenario
import edikgoose.loadgenerator.exception.IllegalConfigException
import edikgoose.loadgenerator.exception.NotFoundException
import edikgoose.loadgenerator.repository.AmmoRepository
import edikgoose.loadgenerator.repository.ScenarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ScenarioService(
    @Autowired val scenarioRepository: ScenarioRepository,
    @Autowired val ammoRepository: AmmoRepository,
    @Autowired val yandexTankTestConfigService: YandexTankTestConfigService
) {
    fun createScenario(name: String, config: String): ScenarioOutputDto {
        val yandexTankConfig = yandexTankTestConfigService.parseYamlConfig(config)

        val scenario = Scenario(id = null, name = name, ammo = null, config = config, createdDate = null)

        yandexTankConfig.phantom?.ammoType?.let {
            when (it) {
                "uri", "uripost", "phantom" -> {
                    val ammo: Ammo? = ammoRepository.findByName(
                        yandexTankConfig.phantom?.ammofile ?: throw IllegalConfigException("ammotype without ammofile")
                    )
                    if (ammo == null) {
                        throw IllegalConfigException("File ${yandexTankConfig.phantom?.ammofile} is not uploaded")
                    } else {
                        scenario.ammo = ammo
                    }
                }
                else -> throw IllegalConfigException("Illegal ammotype param value")
            }
        }

        scenarioRepository.save(scenario)
        return scenario.toScenarioOutputDto()
    }

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
}