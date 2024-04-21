package edikgoose.loadgenerator.ui.ammo.view

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import edikgoose.loadgenerator.dto.AmmoOutputDto
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.service.AmmoService
import edikgoose.loadgenerator.ui.MainLayout
import edikgoose.loadgenerator.ui.ammo.event.AmmoCloseEvent
import edikgoose.loadgenerator.ui.ammo.event.AmmoCreateEvent
import edikgoose.loadgenerator.ui.ammo.form.AmmoForm

@Route("ammo", layout = MainLayout::class)
class AmmoView(
    private val ammoService: AmmoService
) : VerticalLayout() {
    private val grid: Grid<AmmoOutputDto> = Grid(
        AmmoOutputDto::class.java,
    )
    private val filterTextField: TextField = TextField()
    private lateinit var ammoForm: AmmoForm

    init {
        addClassName("table-view")
        setSizeFull()
        add(getToolBar(), getContent())
        updateList()
    }

    private fun getContent(): Component {
        configureGrid()
        configureForm()

        val content = HorizontalLayout(grid, ammoForm)
        content.setFlexGrow(2.0, grid)
        content.setFlexGrow(1.0, ammoForm)
        content.addClassNames("content")
        content.setSizeFull()
        return content
    }

    private fun configureForm() {
        ammoForm = AmmoForm(isForCreation = false)
            .apply {
                width = "25em"
                addCreateListener(this@AmmoView::createAmmo)
                addCloseListener(this@AmmoView::closeForm)
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
            addClassName("ammo-grid")
            setSizeFull()
            setColumns()
            addColumn({ it.id }).setHeader("ID")
            addColumn({ it.name }).setHeader("Name")
            addColumn({ it.ammo }).setHeader("Ammo")
            addColumn({ it.createdDate.toUiFormat() }).setHeader("Created date")

            columns.forEach { it.setAutoWidth(true) }

            asSingleSelect().addValueChangeListener { editAmmo(it.value) }
        }
    }

    private fun editAmmo(dto: AmmoOutputDto?) {
        if (dto != null) {
            openForm(dto)
        }
    }

    private fun createAmmo(ammoCreateEvent: AmmoCreateEvent) {
        closeForm()
        ammoService.createAmmo(name = ammoCreateEvent.name, ammo = ammoCreateEvent.ammo)
        updateList()
    }

    private fun updateList() {
        grid.setItems(ammoService.searchAmmo(filterTextField.value))
    }

    private fun closeForm(event: AmmoCloseEvent? = null) {
//        ammoForm.setAmmo(null)
        ammoForm.isVisible = false
        removeClassName("editing")
    }

    private fun openForm(ammoOutputDto: AmmoOutputDto) {
        ammoForm.setAmmo(ammoOutputDto)
        ammoForm.isVisible = true
        ammoForm.setIsForCreation(false)
        addClassName("editing")
    }

    private fun openFormForCreation() {
        ammoForm.setAmmo(null)
        ammoForm.isVisible = true
        ammoForm.setIsForCreation(true)
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
