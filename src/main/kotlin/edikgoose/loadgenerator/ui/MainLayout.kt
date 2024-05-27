package edikgoose.loadgenerator.ui

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.theme.lumo.LumoUtility
import edikgoose.loadgenerator.ui.ammo.view.AmmoView
import edikgoose.loadgenerator.ui.loadtest.view.LoadTestView
import edikgoose.loadgenerator.ui.scenario.view.ScenarioView
import edikgoose.loadgenerator.ui.systemconfiguration.view.SystemConfigurationView


class MainLayout: AppLayout() {
    init {
        createHeader()
        createDrawer()
    }

    private fun createHeader() {
        val logo = H1("Yandex Tank Load Generator")
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM
        )
        val header = HorizontalLayout(DrawerToggle(), logo)
        header.defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
        header.setWidthFull()
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM
        )
        addToNavbar(header)
    }

    private fun createDrawer() {
        addToDrawer(
            VerticalLayout(
                RouterLink("Load Tests", LoadTestView::class.java),
                RouterLink("Scenarios", ScenarioView::class.java),
                RouterLink("Ammo", AmmoView::class.java),
                RouterLink("System configuration", SystemConfigurationView::class.java),
            )
        )
    }
}