package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.SystemConfigurationDto
import edikgoose.loadgenerator.entity.SystemConfiguration


fun SystemConfiguration.toDto() = SystemConfigurationDto(
    id = id!!,
    name = name!!,
    type = type!!,
    configuration = initialConfiguration!!,
    currentConfiguration = currentConfiguration,
    consulKey = consulKey,
    createdDate = createdDate!!
)