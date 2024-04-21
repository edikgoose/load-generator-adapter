package edikgoose.loadgenerator.ui.scenario.event

import com.vaadin.flow.component.ComponentEvent
import edikgoose.loadgenerator.dto.ScenarioOutputDto
import edikgoose.loadgenerator.ui.scenario.form.ScenarioForm

sealed class ScenarioEvent(
    source: ScenarioForm,
) : ComponentEvent<ScenarioForm>(source, false)

class ScenarioStartEvent(
    source: ScenarioForm,
    val scenarioOutputDto: ScenarioOutputDto,
    val loadTestName: String
) :
    ScenarioEvent(source)

class ScenarioCreateEvent(
    source: ScenarioForm,
    val scenarioOutputDto: ScenarioOutputDto,
    val loadTestName: String
) :
    ScenarioEvent(source)

class ScenarioCloseEvent(source: ScenarioForm) : ScenarioEvent(source)