package edikgoose.loadgenerator.ui.loadtest.view

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.renderer.LitRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import edikgoose.loadgenerator.dto.LoadTestOutputDto
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.enumeration.LoadTestStage
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.service.LoadTestService
import edikgoose.loadgenerator.service.ScenarioService
import edikgoose.loadgenerator.ui.MainLayout
import edikgoose.loadgenerator.ui.loadtest.event.LoadTestCloseEvent
import edikgoose.loadgenerator.ui.loadtest.event.LoadTestStopEvent
import edikgoose.loadgenerator.ui.loadtest.form.LoadTestForm
import edikgoose.loadgenerator.ui.scenario.view.ScenarioView


@Route("", layout = MainLayout::class)
class LoadTestView(
    private val loadTestService: LoadTestService,
    private val scenarioService: ScenarioService,
) : VerticalLayout() {
    private val grid: Grid<LoadTestOutputDto> = Grid(
        LoadTestOutputDto::class.java,
    )
    private val filterTextField: TextField = TextField()
    private lateinit var loadTestForm: LoadTestForm
    private val statusSelect = Select<LoadTestStatus>()
        .apply {
            label = "Status"
            isEmptySelectionAllowed = true
            placeholder = "Status filter"
            setItemLabelGenerator { it?.name ?: "ALL" }
            setItems(LoadTestStatus.values().toMutableList())
        }

    init {
        addClassName("table-view")
        setSizeFull()
        add(getToolBar(), getContent())
        updateList()
        ui.ifPresent {
            it.pollInterval = 3000
            it.addPollListener {
                ui.ifPresent { updateList() }
            }
        }
    }

    private fun getContent(): Component {
        configureGrid()
        configureForm()

        val content = HorizontalLayout(grid, loadTestForm)
        content.setFlexGrow(2.0, grid)
        content.setFlexGrow(1.0, loadTestForm)
        content.addClassNames("content")
        content.setSizeFull()
        return content
    }

    private fun configureForm() {
        loadTestForm = LoadTestForm(scenarioService.getAllScenarios())
            .apply {
                width = "25em"
                addStopListener(this@LoadTestView::stopLoadTest)
                addCloseListener(this@LoadTestView::closeForm)
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
            statusSelect.apply {
                addValueChangeListener { updateList() }
            }
        ).apply {
            alignItems = FlexComponent.Alignment.END
        }
    }

    private fun configureGrid() {
        with(grid) {
            addClassName("load-test-grid")
            setSizeFull()
            setColumns()
            addColumn({ it.id }).setHeader("ID")
            addColumn({ it.name }).setHeader("Name")
            addColumn({ it.status }).setHeader("Status")
            addColumn({ it.stage ?: LoadTestStage.UNKNOWN }).setHeader("Stage")
            addComponentColumn { it: LoadTestOutputDto ->
                RouterLink(it.scenario.id.toString(), ScenarioView::class.java)
            }.setHeader("Scenario ID")
            addColumn({
                if (it.scenario.name.length > 20) {
                    "${it.scenario.name.take(20)}..."
                } else {
                    it.scenario.name
                }
            }).setHeader("Scenario name")
            addColumn({ it.startDate.toUiFormat() }).setHeader("Start date")
            addColumn({ it.finishDate?.toUiFormat() ?: "" }).setHeader("Finish date")
            addColumn(
                LitRenderer.of<LoadTestOutputDto>(LIT_TEMPLATE_HTML)
                    .withFunction("clickHandler") { person ->
                        ui.ifPresent { it.page.open(person.dashboardUrl.toString()) }
                    }
            ).setHeader("Dashboard")
            columns.forEach { it.setAutoWidth(true) }
            ui.ifPresent { it.pollInterval = 3000; it.addPollListener { updateList() } }
            asSingleSelect().addValueChangeListener { editLoadTest(it.value) }
        }
    }

    private fun editLoadTest(dto: LoadTestOutputDto?) {
        if (dto != null) {
            openForm(dto)
        }
    }

    private fun stopLoadTest(event: LoadTestStopEvent) {
        loadTestService.stopLoadTest(event.loadTestOutputDto.id)
        updateList()
        closeForm()
    }

    private fun updateList() {
        grid.setItems(loadTestService.searchLoadTests(filterTextField.value, statusSelect.value))
    }

    private fun closeForm(event: LoadTestCloseEvent? = null) {
        loadTestForm.setLoadTest(null)
        loadTestForm.isVisible = false
        removeClassName("editing")
    }

    private fun openForm(loadTestOutputDto: LoadTestOutputDto) {
        loadTestForm.setLoadTest(loadTestOutputDto)
        loadTestForm.isVisible = true
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
