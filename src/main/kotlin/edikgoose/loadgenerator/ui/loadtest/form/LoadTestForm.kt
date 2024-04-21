package edikgoose.loadgenerator.ui.loadtest.form

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
import edikgoose.loadgenerator.dto.LoadTestOutputDto
import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.ui.loadtest.event.LoadTestCloseEvent
import edikgoose.loadgenerator.ui.loadtest.event.LoadTestStopEvent


class LoadTestForm(
    private val scenarios: List<ScenarioOutputDto>,
) : FormLayout() {
    private val id = TextField("Id")
    private val externalId = TextField("External id")
    private val scenarioId = TextField("Scenario id")
    private val scenarioConfig = TextArea("Scenario config").apply { maxHeight = "300px" }
    private val name = TextField("Name")
    private val status = TextField("Status")
    private val stage = TextField("Stage")
    private val dashboardUrl = TextArea("Grafana")
    private val startDate = TextField("Start date")
    private val finishDate = TextField("Finish date")

    private val stop = Button("Stop")
    private val close = Button("Cancel")

    private val binder = BeanValidationBinder(LoadTestOutputDto::class.java)

    init {
        addClassName("load-test-form")
        configureBinder()
        add(
            id,
            externalId,
            name,
            status,
            stage,
            dashboardUrl,
            scenarioId,
            scenarioConfig,
            startDate,
            finishDate,
            createButtonsLayout()
        )
    }

    private fun configureBinder() {
        with(binder) {
            forField(id).bind({ it.id.toString() }, null)
            forField(externalId).bind({ it.externalId }, null)
            forField(scenarioId).bind({ it.scenario.id.toString() }, null)
            forField(scenarioConfig).bind({ it.scenario.yandexTankConfig }, null)
            forField(name).bind({ it.name }, null)
            forField(status).bind({ it.status.name }, null)
            forField(stage).bind({ it.stage?.name }, null)
            forField(dashboardUrl).bind({ it.dashboardUrl.toExternalForm() }, null)
            forField(startDate).bind({ it.startDate.toUiFormat() }, null)
            forField(finishDate).bind({ it.finishDate?.toUiFormat() }, null)
        }
    }

    private fun createButtonsLayout(): HorizontalLayout {
        stop.addThemeVariants(ButtonVariant.LUMO_ERROR)
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        close.addClickShortcut(Key.ESCAPE)

        stop.addClickListener { fireEvent(LoadTestStopEvent(this, binder.bean)) }

        close.addClickListener { _: ClickEvent<Button> ->
            fireEvent(
                LoadTestCloseEvent(this)
            )
        }


        return HorizontalLayout(stop, close)
    }

    fun setLoadTest(loadTestOutputDto: LoadTestOutputDto?) {
        binder.bean = loadTestOutputDto
    }

    fun addStopListener(listener: ComponentEventListener<LoadTestStopEvent>): Registration {
        return addListener(LoadTestStopEvent::class.java, listener)
    }

    fun addCloseListener(listener: ComponentEventListener<LoadTestCloseEvent>): Registration {
        return addListener(LoadTestCloseEvent::class.java, listener)
    }
}