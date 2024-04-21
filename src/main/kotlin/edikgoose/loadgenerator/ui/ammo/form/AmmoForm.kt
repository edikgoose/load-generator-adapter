package edikgoose.loadgenerator.ui.ammo.form

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.shared.Registration
import edikgoose.loadgenerator.dto.AmmoOutputDto
import edikgoose.loadgenerator.dto.toUiFormat
import edikgoose.loadgenerator.ui.ammo.event.AmmoCloseEvent
import edikgoose.loadgenerator.ui.ammo.event.AmmoCreateEvent

class AmmoForm(
    private var isForCreation: Boolean
) : FormLayout() {
    private val id = IntegerField("Id").apply {
        if (isForCreation) {
            isVisible = false
        }
    }
    private val name = TextField("Name")
    private val ammo = TextArea("Ammo").apply { maxHeight = "300px" }
    private val createdDate = TextField("Created date")

    private val create = Button("Create")
    private val close = Button("Close")

    private val binder = BeanValidationBinder(AmmoOutputDto::class.java)

    init {
        addClassName("ammo-form")
        configureBinder()
        add(
            id,
            name,
            ammo,
            createdDate,
            createButtonsLayout()
        )
    }

    fun setIsForCreation(isForCreation: Boolean) {
        this.isForCreation = isForCreation
        if (isForCreation) {
            setAmmo(null)
            binder.setReadOnly(false)
            id.isVisible = false
            createdDate.isVisible = false
            create.isVisible = true
        } else {
            binder.setReadOnly(true)
            id.isVisible = true
            createdDate.isVisible = false
            create.isVisible = false
        }
    }

    private fun configureBinder() {
        with(binder) {
            forField(id).bind({ it.id.toInt() }, { dto, id -> dto.id = id.toLong() })
            forField(name).bind("name")
            forField(ammo).bind("ammo")
            forField(createdDate).bind({ it.createdDate.toUiFormat() }, null)

            if (!isForCreation) {
                setReadOnly(true)
            }
        }
    }

    private fun createButtonsLayout(): HorizontalLayout {
        if (!isForCreation) {
            create.isVisible = false
        }
        create.addThemeVariants(ButtonVariant.LUMO_ERROR)
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        close.addClickShortcut(Key.ESCAPE)

        create.addClickListener {
            if (binder.isValid || (isForCreation && !id.isEmpty && !name.isEmpty)) {
                fireEvent(AmmoCreateEvent(this, name = name.value, ammo = ammo.value))
            }
        }

        close.addClickListener { _: ClickEvent<Button> ->
            fireEvent(
                AmmoCloseEvent(this)
            )
        }

        return HorizontalLayout(create, close)
    }

    fun setAmmo(ammoOutputDto: AmmoOutputDto?) {
        binder.bean = ammoOutputDto
    }

    fun addCreateListener(listener: ComponentEventListener<AmmoCreateEvent>): Registration {
        return addListener(AmmoCreateEvent::class.java, listener)
    }

    fun addCloseListener(listener: ComponentEventListener<AmmoCloseEvent>): Registration {
        return addListener(AmmoCloseEvent::class.java, listener)
    }
}