package edikgoose.loadgenerator.ui.systemconfiguration.view

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.dto.SystemConfigurationDto
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.service.ConsulKvService
import edikgoose.loadgenerator.service.LoadTestService
import edikgoose.loadgenerator.service.ScenarioService
import edikgoose.loadgenerator.service.SystemConfigurationService
import edikgoose.loadgenerator.ui.MainLayout
import edikgoose.loadgenerator.ui.loadtest.view.LoadTestView
import edikgoose.loadgenerator.ui.scenario.event.ScenarioCloseEvent
import edikgoose.loadgenerator.ui.scenario.event.ScenarioCreateEvent
import edikgoose.loadgenerator.ui.scenario.event.ScenarioStartEvent
import edikgoose.loadgenerator.ui.systemconfiguration.event.SystemConfigurationCloseEvent
import edikgoose.loadgenerator.ui.systemconfiguration.event.SystemConfigurationCreateEvent
import edikgoose.loadgenerator.ui.systemconfiguration.event.SystemConfigurationUpdateEvent
import edikgoose.loadgenerator.ui.systemconfiguration.form.SystemConfigurationForm


@Route("configuration", layout = MainLayout::class)
class SystemConfigurationView(
    private val systemConfigurationService: SystemConfigurationService,
    private val consulKvService: ConsulKvService
) : VerticalLayout() {
    private val grid: Grid<SystemConfigurationDto> = Grid(
        SystemConfigurationDto::class.java,
    )
    private val filterTextField: TextField = TextField()
    private val systemConfigurationForm: SystemConfigurationForm = SystemConfigurationForm(consulKvService)

    init {
        addClassName("table-view")
        setSizeFull()
        add(getToolBar(), getContent())
        updateList()
    }

    private fun getContent(): Component {
        configureGrid()
        configureForm()

        val content = HorizontalLayout(grid, systemConfigurationForm)
        content.setFlexGrow(2.0, grid)
        content.setFlexGrow(1.0, systemConfigurationForm)
        content.addClassNames("content")
        content.setSizeFull()
        return content
    }

    private fun configureForm() {
        with(systemConfigurationForm) {
            width = "25em"
            addCreateListener(this@SystemConfigurationView::createSystemConfig)
            addCloseListener(this@SystemConfigurationView::closeForm)
            addUpdateListener(this@SystemConfigurationView::updateConsulCurrent)
        }
        closeForm()
    }

    private fun getToolBar(): HorizontalLayout {
        return HorizontalLayout(
            filterTextField.apply {
                placeholder = "Filter by name..."
                isClearButtonVisible = true
                valueChangeMode = ValueChangeMode.EAGER
                addValueChangeListener { updateList() }
            },
            Button("Add").apply { addClickListener { openFormForCreation() } },
        ).apply {
            alignItems = FlexComponent.Alignment.END
        }
    }

    private fun configureGrid() {
        with(grid) {
            addClassName("scenario-grid")
            setSizeFull()
            setColumns()
            addColumn({ it.id }).setHeader("ID")
            addColumn({ it.name }).setHeader("Name")
            addColumn({ it.type.name }).setHeader("Type")
            addColumn({
                if (it.configuration.length > 20) {
                    "${it.configuration.take(20)}..."
                } else {
                    it.configuration.toString()
                }
            }).setHeader("Initial configuration")
            addColumn({ it.consulKey ?: "No" }).setHeader("Consul key")
            addColumn({ it.createdDate.toUiFormat() }).setHeader("Created date")

            columns.forEach { it.setAutoWidth(true) }

            asSingleSelect().addValueChangeListener { editScenario(it.value) }
        }
    }

    private fun editScenario(dto: SystemConfigurationDto?) {
        if (dto != null) {
            openForm(dto)
        }
    }

    private fun createSystemConfig(event: SystemConfigurationCreateEvent) {
        val outputDto = if (event.isConsul) {
            systemConfigurationService.createConsulConfig(event.name, event.consulKey!!, event.initialConfiguration)
        } else {
            systemConfigurationService.createDefaultConfig(event.name, event.initialConfiguration)
        }
        updateList()
        openForm(outputDto)
    }

    private fun updateList() {
        grid.setItems(systemConfigurationService.searchScenarios(filterTextField.value))
    }

    private fun closeForm(event: SystemConfigurationCloseEvent? = null) {
        systemConfigurationForm.setSystemConfig(null, null)
        systemConfigurationForm.isVisible = false
        removeClassName("editing")
    }

    private fun updateConsulCurrent(event: SystemConfigurationUpdateEvent) {
        systemConfigurationService.updateCurrentConfig(
            config = event.currentConfig,
            id = event.id,
            consulKey = event.consulKey
        )
    }

    private fun openForm(systemConfigurationDto: SystemConfigurationDto) {
        systemConfigurationForm.isForCreation = false
        systemConfigurationForm.setSystemConfig(
            systemConfigurationDto,
            if (systemConfigurationDto.consulKey == null) {
                consulKvService.getValue(systemConfigurationDto.consulKey!!)
            } else { null }
        )
        systemConfigurationForm.isVisible = true
        addClassName("editing")
    }

    private fun openFormForCreation() {
        systemConfigurationForm.isForCreation = true
        systemConfigurationForm.setSystemConfig(null, null)
        systemConfigurationForm.isVisible = true
        addClassName("editing")
    }

    companion object {
        private val LIT_TEMPLATE_HTML = """
            <vaadin-button title="Go to Grafana dashboard"
                           @click="${'$'}{clickHandler}"
                           theme="tertiary-inline small link">
                  Grafana
            </vaadin-button>
            """.trimIndent()
    }
}
