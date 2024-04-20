package edikgoose.loadgenerator.ui.event

import com.vaadin.flow.component.ComponentEvent
import edikgoose.loadgenerator.dto.LoadTestOutputDto
import edikgoose.loadgenerator.ui.form.LoadTestForm

sealed class LoadTestEvent(
    source: LoadTestForm,
) : ComponentEvent<LoadTestForm>(source, false)

class StopEvent(source: LoadTestForm, val loadTestOutputDto: LoadTestOutputDto) : LoadTestEvent(source)
class CloseEvent(source: LoadTestForm) : LoadTestEvent(source)





