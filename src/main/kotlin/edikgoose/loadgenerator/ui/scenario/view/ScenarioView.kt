package edikgoose.loadgenerator.ui.scenario.view

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
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.service.LoadTestService
import edikgoose.loadgenerator.service.ScenarioService
import edikgoose.loadgenerator.ui.MainLayout
import edikgoose.loadgenerator.ui.loadtest.view.LoadTestView
import edikgoose.loadgenerator.ui.scenario.event.ScenarioCloseEvent
import edikgoose.loadgenerator.ui.scenario.event.ScenarioStartEvent
import edikgoose.loadgenerator.ui.scenario.form.ScenarioForm


@Route("scenarios", layout = MainLayout::class)
class ScenarioView(
    private val scenarioService: ScenarioService,
    private val loadTestService: LoadTestService
) : VerticalLayout() {
    private val grid: Grid<ScenarioOutputDto> = Grid(
        ScenarioOutputDto::class.java,
    )
    private val filterTextField: TextField = TextField()
    private lateinit var scenarioForm: ScenarioForm

    init {
        addClassName("table-view")
        setSizeFull()
        add(getToolBar(), getContent())
        updateList()
    }

    private fun getContent(): Component {
        configureGrid()
        configureForm()

        val content = HorizontalLayout(grid, scenarioForm)
        content.setFlexGrow(2.0, grid)
        content.setFlexGrow(1.0, scenarioForm)
        content.addClassNames("content")
        content.setSizeFull()
        return content
    }

    private fun configureForm() {
        scenarioForm = ScenarioForm()
            .apply {
                width = "25em"
                addStartListener(this@ScenarioView::startLoadTestByScenario)
                addCloseListener(this@ScenarioView::closeForm)
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
            Button("Add"),
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
            addColumn({ "${it.yandexTankConfig.take(20)}..." }).setHeader("Yandex tank config")
            addColumn({ it.ammoId.toString() }).setHeader("Ammo id")
            addColumn({ it.createdDate.toUiFormat() }).setHeader("Created date")
            addColumn({ it.systemConfigurationDto?.id.toString() }).setHeader("System configuration ID")

            columns.forEach { it.setAutoWidth(true) }

            asSingleSelect().addValueChangeListener { editScenario(it.value) }
        }
    }

    private fun editScenario(dto: ScenarioOutputDto?) {
        if (dto != null) {
            openForm(dto)
        }
    }

    private fun startLoadTestByScenario(event: ScenarioStartEvent) {
        closeForm()
        loadTestService.runLoadTest(event.scenarioOutputDto.id, event.loadTestName)
        updateList()
        ui.ifPresent { it.navigate(LoadTestView::class.java) }
    }

    private fun updateList() {
        grid.setItems(scenarioService.searchScenarios(filterTextField.value))
    }

    private fun closeForm(event: ScenarioCloseEvent? = null) {
        scenarioForm.setScenario(null)
        scenarioForm.isVisible = false
        removeClassName("editing")
    }

    private fun openForm(scenarioOutputDto: ScenarioOutputDto) {
        scenarioForm.setScenario(scenarioOutputDto)
        scenarioForm.isVisible = true
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
