package edikgoose.loadgenerator.ui.systemconfiguration.form

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.shared.Registration
import edikgoose.loadgenerator.dto.SystemConfigurationDto
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.enumeration.SystemConfigurationType
import edikgoose.loadgenerator.service.ConsulKvService
import edikgoose.loadgenerator.ui.systemconfiguration.event.SystemConfigurationCloseEvent
import edikgoose.loadgenerator.ui.systemconfiguration.event.SystemConfigurationCreateEvent
import edikgoose.loadgenerator.ui.systemconfiguration.event.SystemConfigurationUpdateEvent

class SystemConfigurationForm (
    private val consulKvService: ConsulKvService
) : FormLayout() {
    var isForCreation: Boolean = false
        set(value) {
            if (value) {
                binder.bean = null
                id.isVisible = false
                createdDate.isVisible = false
                create.isVisible = true
                update.isVisible = false
                currentConfig.isVisible = false
                isConsul.isReadOnly = false
                binder.setReadOnly(false)
            } else {
                id.isVisible = true
                createdDate.isVisible = true
                create.isVisible = false
                update.isVisible = true
                currentConfig.isVisible = true
                isConsul.isReadOnly = true
                getConsulValue.isVisible = false
                binder.setReadOnly(true)
            }
            field = value
        }
    private val id = TextField("Id")
    private val name = TextField("Name")
    private val initialConfig = TextArea("Initial configuration").apply { maxHeight = "300px" }
    private val currentConfig = TextArea("Current configuration").apply { maxHeight = "300px" }
    private val consulKey = TextField("Consul key").apply { isVisible = false }
    private val createdDate = TextField("Created date")

    private var isConsul = Checkbox("Config from Consul?")
    private val getConsulValue = Button("Poll Consul config").apply { isVisible = false }

    private val create = Button("Create")
    private val close = Button("Close")
    private val update = Button("Update current")

    private val binder = BeanValidationBinder(SystemConfigurationDto::class.java)

    init {
        addClassName("scenario-form")
        configureBinder()
        add(
            id,
            name,
            initialConfig,
            currentConfig,
            isConsul,
            consulKey,
            getConsulValue,
            createdDate,
            createButtonsLayout()
        )

        isConsul.addValueChangeListener {
            if (isConsul.value) {
                initialConfig.clear()
                initialConfig.isReadOnly = true
                if (isForCreation) getConsulValue.isVisible = true
                consulKey.isVisible = true
            } else {
                initialConfig.isReadOnly = false
                getConsulValue.isVisible = false
                consulKey.isVisible = false
            }
        }

        consulKey.addValueChangeListener {
            initialConfig.value = ""
        }

        getConsulValue.isVisible = false
        getConsulValue.addClickListener {
            if (!consulKey.isEmpty) {
                try {
                    val value = consulKvService.getValue(consulKey.value)
                    initialConfig.value = value
                } catch (e: Exception) {
                    Notification.show("Error during polling key: ${consulKey.value}")
                }
            }
        }
    }

    private fun configureBinder() {
        with(binder) {
            forField(id).bind({ it.id.toString() }, null)
            forField(name).bind({ it.name },
                { dto, name -> dto.name = name })
            forField(consulKey).bind({ it.consulKey },
                { dto, key -> dto.consulKey = key })
            forField(initialConfig)
                .bind({ it.configuration },
                    { dto, config -> dto.configuration = config })
            forField(createdDate).bind({ it.createdDate.toUiFormat() }, null)
        }
    }

    private fun createButtonsLayout(): HorizontalLayout {
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        close.addClickShortcut(Key.ESCAPE)

        create.addClickListener {
            if (!name.isEmpty && !initialConfig.isEmpty
                && (!isConsul.value || (isConsul.value && !consulKey.isEmpty))
            ) {
                fireEvent(
                    SystemConfigurationCreateEvent(
                        source = this,
                        name = name.value,
                        initialConfiguration = initialConfig.value,
                        isConsul = isConsul.value,
                        consulKey = consulKey.value
                    )
                )
            }
        }

        update.addClickListener {
            if (!isForCreation && isConsul.value && !currentConfig.isEmpty) {
                fireEvent(
                    SystemConfigurationUpdateEvent(
                        source = this,
                        currentConfig = currentConfig.value,
                        isConsul = true,
                        consulKey = consulKey.value,
                        id = id.value!!.toLong()
                    )
                )
            }
        }

        close.addClickListener { _: ClickEvent<Button> ->
            fireEvent(
                SystemConfigurationCloseEvent(this)
            )
        }

        return HorizontalLayout(create, update, close)
    }

    fun setSystemConfig(systemConfigurationDto: SystemConfigurationDto?, currentConsulConfig: String?) {
        isConsul.value = systemConfigurationDto?.type == SystemConfigurationType.CONSUL
        binder.bean = systemConfigurationDto
        currentConfig.value = currentConsulConfig ?: ""
    }

    fun addCreateListener(listener: ComponentEventListener<SystemConfigurationCreateEvent>): Registration {
        return addListener(SystemConfigurationCreateEvent::class.java, listener)
    }

    fun addCloseListener(listener: ComponentEventListener<SystemConfigurationCloseEvent>): Registration {
        return addListener(SystemConfigurationCloseEvent::class.java, listener)
    }

    fun addUpdateListener(listener: ComponentEventListener<SystemConfigurationUpdateEvent>): Registration {
        return addListener(SystemConfigurationUpdateEvent::class.java, listener)
    }
}