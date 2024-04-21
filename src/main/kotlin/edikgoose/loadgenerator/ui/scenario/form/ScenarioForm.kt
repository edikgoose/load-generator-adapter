package edikgoose.loadgenerator.ui.scenario.form

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.shared.Registration
import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.ui.scenario.event.ScenarioCloseEvent
import edikgoose.loadgenerator.ui.scenario.event.ScenarioCreateEvent
import edikgoose.loadgenerator.ui.scenario.event.ScenarioStartEvent

class ScenarioForm : FormLayout() {
    var isForCreation: Boolean = false
        set(value) {
            if (value) {
                binder.bean = null
                id.isVisible = false
                systemConfiguration.isVisible = false
                createdDate.isVisible = false
                loadTestName.isVisible = false
                create.isVisible = true
                start.isVisible = false
                binder.setReadOnly(false)
                scenarioConfig.value = INITIAL_CONFIG
                scenarioConfig.helperText = "You should modify only part with load generator. influx, console, etc. will be configured automatically"
            } else {
                id.isVisible = true
                systemConfiguration.isVisible = true
                createdDate.isVisible = true
                loadTestName.isVisible = true
                create.isVisible = false
                start.isVisible = true
                binder.setReadOnly(true)
                scenarioConfig.helperText = ""
            }
            field = value
        }
    private val id = TextField("Id")
    private val name = TextField("Name")
    private val scenarioConfig = TextArea("Scenario config").apply {
        maxHeight = "300px"
    }
    private val createdDate = TextField("Created date")
    private val ammoId = IntegerField("Ammo ID")
    private val systemConfigurationId = IntegerField("System configuration ID")
    private val systemConfiguration = TextArea("System configuration").apply { maxHeight = "300px" }

    private val loadTestName = TextField("").apply { label = "Name for new load test" }

    private val create = Button("Create")
    private val start = Button("Start")
    private val close = Button("Close")

    private val binder = BeanValidationBinder(ScenarioOutputDto::class.java)

    init {
        addClassName("scenario-form")
        configureBinder()
        add(
            id,
            name,
            scenarioConfig,
            ammoId,
            systemConfigurationId,
            systemConfiguration,
            createdDate,
            loadTestName,
            createButtonsLayout()
        )
    }

    private fun configureBinder() {
        with(binder) {
            forField(id).bind({ it.id.toString() }, null)
            forField(scenarioConfig).bind({ it.yandexTankConfig },
                { dto, yandexTankConfig -> dto.yandexTankConfig = yandexTankConfig })
            forField(name).bind({ it.name },
                { dto, name -> dto.name = name })
            forField(ammoId).bind({ it.ammoId?.toInt() },
                { dto, ammoId -> dto.ammoId = ammoId.toLong() })
            forField(systemConfigurationId)
                .bind({ it.systemConfigurationDto?.id?.toInt() },
                    { dto, configId -> dto.systemConfigurationDto = null }) // TODO
            forField(systemConfiguration)
                .bind({ it.systemConfigurationDto?.configuration.toString() }, null)
            forField(createdDate).bind({ it.createdDate.toUiFormat() }, null)
        }
    }

    private fun createButtonsLayout(): HorizontalLayout {
        start.addThemeVariants(ButtonVariant.LUMO_ERROR)
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        close.addClickShortcut(Key.ESCAPE)

        create.addClickListener {
            if (isForCreation && !name.isEmpty
                && !scenarioConfig.isEmpty
            ) {
                fireEvent(
                    ScenarioCreateEvent(
                        source = this,
                        name = name.value,
                        scenarioConfig = scenarioConfig.value,
                        ammoId = ammoId.value?.toLong(),
                        systemConfigId = systemConfigurationId.value?.toLong()
                    )
                )
            }
        }

        start.addClickListener {
            if (!loadTestName.isEmpty) {
                fireEvent(ScenarioStartEvent(this, binder.bean, loadTestName.value))
            }
        }

        close.addClickListener { _: ClickEvent<Button> ->
            fireEvent(
                ScenarioCloseEvent(this)
            )
        }

        return HorizontalLayout(create, start, close)
    }

    fun setScenario(scenarioOutputDto: ScenarioOutputDto?) {
        binder.bean = scenarioOutputDto
    }

    fun addCreateListener(listener: ComponentEventListener<ScenarioCreateEvent>): Registration {
        return addListener(ScenarioCreateEvent::class.java, listener)
    }

    fun addStartListener(listener: ComponentEventListener<ScenarioStartEvent>): Registration {
        return addListener(ScenarioStartEvent::class.java, listener)
    }

    fun addCloseListener(listener: ComponentEventListener<ScenarioCloseEvent>): Registration {
        return addListener(ScenarioCloseEvent::class.java, listener)
    }

    companion object {
        const val INITIAL_CONFIG = """phantom:
  address: <HOST>:<PORT>
  header_http: "1.1"
  headers:
    - "[Host: <HOST>]"
    - "[Connection: close]"
  uris:
    - /<path>
  load_profile:
    load_type: rps
    schedule: line(<Initial rps>, <Final rps>, 5m)"""
    }
}