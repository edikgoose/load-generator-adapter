package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.converter.toDto
import edikgoose.loadgenerator.dto.SystemConfigurationDto
import edikgoose.loadgenerator.entity.SystemConfiguration
import edikgoose.loadgenerator.enumeration.SystemConfigurationType
import edikgoose.loadgenerator.exception.NotFoundException
import edikgoose.loadgenerator.repository.SystemConfigurationRepository
import org.springframework.stereotype.Service

@Service
class SystemConfigurationService(
    private val systemConfigurationRepository: SystemConfigurationRepository
) {
    fun createDefaultConfig(name: String, config: String): SystemConfigurationDto {
        val systemConfiguration = SystemConfiguration(
            name = name,
            type = SystemConfigurationType.TEXT,
            configuration = config,
            consulKey = null,
            createdDate = null
        )
        return systemConfigurationRepository.save(systemConfiguration).toDto()
    }

    fun createConsulConfig(name: String, consulKey: String): SystemConfigurationDto {
        // TODO: Сделать запрос в консул. Если не 200, то отдаем 400
        // Сохранить конфиг в поле сonfig
        val systemConfiguration = SystemConfiguration(
            name = name,
            type = SystemConfigurationType.CONSUL,
            configuration = null,
            consulKey = consulKey,
            createdDate = null
        )
        return systemConfigurationRepository.save(systemConfiguration).toDto()
    }

    fun getConfig(id: Long): SystemConfigurationDto {
        return systemConfigurationRepository.findById(id).orElseThrow {
            throw NotFoundException(id, SystemConfiguration::class.java)
        }.toDto()
    }
}