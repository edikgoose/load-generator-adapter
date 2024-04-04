package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.converter.toDto
import edikgoose.loadgenerator.dto.SystemConfigurationDto
import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.entity.SystemConfiguration
import edikgoose.loadgenerator.enumeration.SystemConfigurationType
import edikgoose.loadgenerator.exception.ConsulErrorException
import edikgoose.loadgenerator.exception.ConsulKeyValueNotFoundAndConfigNotPutException
import edikgoose.loadgenerator.exception.ConsulKeyValueNotFoundException
import edikgoose.loadgenerator.exception.NotFoundException
import edikgoose.loadgenerator.repository.LoadTestRepository
import edikgoose.loadgenerator.repository.SystemConfigurationRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

@Service
class SystemConfigurationService(
    private val systemConfigurationRepository: SystemConfigurationRepository,
    private val loadTestRepository: LoadTestRepository,
    private val consulKvService: ConsulKvService,
    private val transactionTemplate: TransactionTemplate
) {
    val logger: Logger = LoggerFactory.getLogger(SystemConfigurationService::class.java)

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

    fun createConsulConfig(name: String, consulKey: String, config: String?): SystemConfigurationDto {
        var configurationFromConsul: String? = null
        try {
            configurationFromConsul = consulKvService.getValue(consulKey)
        } catch (ex: ConsulKeyValueNotFoundException) {
            logger.info("Consul KV does not have such key $consulKey. Will be created")
            consulKvService.putValue(
                consulKey,
                config ?: throw ConsulKeyValueNotFoundAndConfigNotPutException(key = consulKey)
            )
        } catch (ex: RuntimeException) {
            logger.error("Error during config resulution in consul: ", ex)
            throw ConsulErrorException(ex)
        }

        // Сохранить конфиг в поле сonfig
        val systemConfiguration = SystemConfiguration(
            name = name,
            type = SystemConfigurationType.CONSUL,
            configuration = configurationFromConsul ?: config,
            consulKey = consulKey,
            createdDate = null
        )
        return systemConfigurationRepository.save(systemConfiguration).toDto()
    }

    fun pollConfiguration(loadTestId: Long) {
        val systemConfiguration: SystemConfiguration = loadTestRepository.findConfigurationById(loadTestId) ?:
            throw NotFoundException(loadTestId, LoadTest::class.java)

        if (systemConfiguration.type == SystemConfigurationType.CONSUL) {
            logger.info("Start polling consul config for load test $loadTestId")
            val configurationFromConsul = consulKvService.getValue(systemConfiguration.consulKey!!)
            systemConfiguration.configuration = configurationFromConsul
            systemConfigurationRepository.save(systemConfiguration)
        }
    }

    fun getConfig(id: Long): SystemConfigurationDto {
        return systemConfigurationRepository.findById(id).orElseThrow {
            throw NotFoundException(id, SystemConfiguration::class.java)
        }.toDto()
    }

    fun updateConfig(id: Long, config: String): SystemConfigurationDto {
        val consulKey = transactionTemplate.execute {
            val systemConfiguration = systemConfigurationRepository.findById(id).orElse(null)
                ?: throw NotFoundException(id, SystemConfiguration::class.java)

            systemConfiguration.configuration = config
            systemConfigurationRepository.save(systemConfiguration)

            systemConfiguration.consulKey
        }

        consulKey?.let {
            try {
                consulKvService.putValue(it, config)
            } catch (ex: RuntimeException) {
                logger.error("Error during config put in Consul", ex)
                throw ConsulErrorException(ex)
            }
        }

        return systemConfigurationRepository.findById(id).orElseThrow().toDto()
    }
}