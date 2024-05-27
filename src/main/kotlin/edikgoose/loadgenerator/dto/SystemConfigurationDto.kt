package edikgoose.loadgenerator.dto

import com.fasterxml.jackson.annotation.JsonInclude
import edikgoose.loadgenerator.enumeration.SystemConfigurationType
import org.jetbrains.annotations.NotNull
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SystemConfigurationDto (
    val id: Long,
    var name: String,
    var type: SystemConfigurationType,
    var configuration: String,
    var currentConfiguration: String?,
    var consulKey: String?,
    val createdDate: Instant
)
