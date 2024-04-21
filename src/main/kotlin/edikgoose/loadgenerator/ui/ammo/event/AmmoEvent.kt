package edikgoose.loadgenerator.ui.ammo.event

import com.vaadin.flow.component.ComponentEvent
import edikgoose.loadgenerator.ui.ammo.form.AmmoForm

sealed class AmmoEvent(
    source: AmmoForm,
) : ComponentEvent<AmmoForm>(source, false)

class AmmoCreateEvent(
    source: AmmoForm,
    val name: String,
    val ammo: String,
) : AmmoEvent(source)

class AmmoCloseEvent(source: AmmoForm) : AmmoEvent(source)