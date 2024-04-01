package edikgoose.loadgenerator.dto

import com.fasterxml.jackson.annotation.JsonInclude
import edikgoose.loadgenerator.enumeration.SystemConfigurationType
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SystemConfigurationDto (
    val id: Long,
    val name: String,
    val type: SystemConfigurationType,
    val configuration: String?,
    val consulKey: String?,
    val createdDate: Instant
)
