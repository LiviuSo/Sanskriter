package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.annotation.IdRes
import com.android.lvicto.sanskritkeyboard.keyboard.initializer.KbLayoutInitializer
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType
import android.graphics.Rect
import android.view.Gravity
import android.widget.PopupWindow
import android.widget.TextView


class CustomKeyboard2 : InputMethodService(), KeyboardSwitch {

    private var currentKeyboardType = KeyboardType.QWERTY

    override fun switchKeyboard() {
        currentKeyboardType = if(currentKeyboardType == KeyboardType.QWERTY) {
            KeyboardType.SA
        } else {
            KeyboardType.QWERTY
        }
        onInitializeInterface()
        onBindInput()
        setInputView(kbLayoutInitializer.initKeyboard())
        onStartInput(null, true) // todo investigate if possibly called with these params
    }

    private lateinit var kbLayoutInitializer: KbLayoutInitializer

    override fun onInitializeInterface() {
        Log.d(LOG_TAG, "onInitializeInterface()")
        val kbConfig = KeyboardConfig(applicationContext.isTablet(), applicationContext.getOrientation(), currentKeyboardType)
        kbLayoutInitializer = KbLayoutInitializer.getLayoutInitializer(applicationContext, kbConfig)!!
        kbLayoutInitializer.keyboardSwitch = this
    }

    override fun onBindInput() {
        Log.d(LOG_TAG, "onBindInput()")
        if(currentInputConnection != null) {
            kbLayoutInitializer.ic = currentInputConnection
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInput()")
        kbLayoutInitializer.currentInputEditorInfo = currentInputEditorInfo
        if (attribute == null && restarting) {
            kbLayoutInitializer.setActionType()
        }
    }

    override fun onCreateInputView(): View {
        Log.d(LOG_TAG, "onCreateInputView()")
        if(currentInputConnection != null) {
            kbLayoutInitializer.ic = currentInputConnection
        }
        return kbLayoutInitializer.initKeyboard()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInputView()")
        if(currentInputConnection != null) {
            kbLayoutInitializer.ic = currentInputConnection
        }
        if (info != null) {
            kbLayoutInitializer.currentInputEditorInfo = info
            kbLayoutInitializer.setActionType()
        }
    }

    companion object {
        const val LOG_TAG = "CustomKeyboard2"
    }
}

interface KeyboardSwitch {
    fun switchKeyboard()
}

fun Context.getOrientation(): Int = this.resources?.configuration?.orientation!!

fun Context.isTablet(): Boolean {
    val xlarge = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == 4
    val large = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
    return xlarge || large
}

fun Int.getVal(context: Context): Int = context.resources.getInteger(this)

fun Context.layoutInflater(): LayoutInflater =
        this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

infix fun View.button(@IdRes id: Int): Button = (this.findViewById(id) as Button)

fun View.locateView(): Rect {
    val locInt = IntArray(2)
    try {
        this.getLocationInWindow(locInt)
    } catch (npe: Throwable) {
        // Happens when the view doesn't exist on screen anymore.
        return Rect(-1, -1, -1, -1)
    }

    val location = Rect()
    location.left = locInt[0]
    location.top = locInt[1]
    location.right = location.left + this.width
    location.bottom = location.top + this.height
    return location
}

fun View.createPopup(text: String): PopupWindow {
    val context = this.context
    val popup = PopupWindow(this)
    popup.contentView = context.layoutInflater().inflate(R.layout.popup_key_preview, null)
    popup.isOutsideTouchable = true
    popup.contentView.findViewById<TextView>(R.id.tvPreview).text = text
    return popup
}

fun PopupWindow.show(parent: View, rect: Rect) {
    val width = rect.bottom - rect.top
    val height = rect.right - rect.left
    this.showAtLocation(parent.rootView, Gravity.START or Gravity.TOP, rect.left, rect.top - height)
    this.update(width, height)
}
