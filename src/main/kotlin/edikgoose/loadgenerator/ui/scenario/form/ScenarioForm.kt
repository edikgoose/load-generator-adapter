package edikgoose.loadgenerator.ui.scenario.form

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.shared.Registration
import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.ui.scenario.event.ScenarioCloseEvent
import edikgoose.loadgenerator.ui.scenario.event.ScenarioStartEvent

class ScenarioForm : FormLayout() {
    private val id = TextField("Id")
    private val name = TextField("Name")
    private val scenarioConfig = TextArea("Scenario config").apply { maxHeight = "300px" }
    private val createdDate = TextField("Created date")
    private val ammoId = TextField("Ammo ID")
    private val systemConfigurationId = TextField("System configuration ID")
    private val systemConfiguration = TextArea("System configuration").apply { maxHeight = "300px" }

    private val loadTestName = TextField("").apply { label = "Name for new load test" }

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
            forField(scenarioConfig).bind({ it.yandexTankConfig }, null)
            forField(name).bind({ it.name }, null)
            forField(ammoId).bind({ it.ammoId.toString() }, null)
            forField(systemConfigurationId)
                .bind({ it.systemConfigurationDto?.id.toString() }, null)
            forField(systemConfiguration)
                .bind({ it.systemConfigurationDto?.configuration.toString() }, null)
            forField(createdDate).bind({ it.createdDate.toUiFormat() }, null)
        }
    }

    private fun createButtonsLayout(): HorizontalLayout {
        start.addThemeVariants(ButtonVariant.LUMO_ERROR)
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        close.addClickShortcut(Key.ESCAPE)

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

        return HorizontalLayout(start, close)
    }

    fun setScenario(scenarioOutputDto: ScenarioOutputDto?) {
        binder.bean = scenarioOutputDto
    }

    fun addStartListener(listener: ComponentEventListener<ScenarioStartEvent>): Registration {
        return addListener(ScenarioStartEvent::class.java, listener)
    }

    fun addCloseListener(listener: ComponentEventListener<ScenarioCloseEvent>): Registration {
        return addListener(ScenarioCloseEvent::class.java, listener)
    }
}