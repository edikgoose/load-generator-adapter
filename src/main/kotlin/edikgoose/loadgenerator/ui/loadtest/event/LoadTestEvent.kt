package edikgoose.loadgenerator.ui.loadtest.event

import com.vaadin.flow.component.ComponentEvent
import edikgoose.loadgenerator.dto.LoadTestOutputDto
import edikgoose.loadgenerator.ui.loadtest.form.LoadTestForm

sealed class LoadTestEvent(
    source: LoadTestForm,
) : ComponentEvent<LoadTestForm>(source, false)

class LoadTestStopEvent(source: LoadTestForm, val loadTestOutputDto: LoadTestOutputDto) : LoadTestEvent(source)
class LoadTestCloseEvent(source: LoadTestForm) : LoadTestEvent(source)



