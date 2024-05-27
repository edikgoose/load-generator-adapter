package edikgoose.loadgenerator.ui.systemconfiguration.event

import com.vaadin.flow.component.ComponentEvent
import edikgoose.loadgenerator.ui.systemconfiguration.form.SystemConfigurationForm

sealed class SystemConfigurationEvent(
    source: SystemConfigurationForm,
) : ComponentEvent<SystemConfigurationForm>(source, false)

class SystemConfigurationCreateEvent(
    source: SystemConfigurationForm,
    val name: String,
    val initialConfiguration: String,
    val isConsul: Boolean,
    val consulKey: String?,
) : SystemConfigurationEvent(source)

class SystemConfigurationCloseEvent(source: SystemConfigurationForm) : SystemConfigurationEvent(source)

class SystemConfigurationUpdateEvent(
    source: SystemConfigurationForm,
    val currentConfig: String,
    val isConsul: Boolean,
    val id: Long,
    val consulKey: String,
) : SystemConfigurationEvent(source)
