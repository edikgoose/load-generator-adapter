package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.SystemConfiguration
import org.springframework.data.jpa.repository.JpaRepository

interface SystemConfigurationRepository: JpaRepository<SystemConfiguration, Long>