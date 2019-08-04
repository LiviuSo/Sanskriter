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
        setInputView(kbLayoutInitializer!!.initKeyboard()!!)
    }

    private var kbLayoutInitializer: KbLayoutInitializer? = null

    override fun onInitializeInterface() {
        Log.d(LOG_TAG, "onInitializeInterface()")
        val kbConfig = KeyboardConfig(applicationContext.isTablet(), applicationContext.getOrientation(), currentKeyboardType)
        kbLayoutInitializer = KbLayoutInitializer.getLayoutInitializer(applicationContext, kbConfig)!!
        kbLayoutInitializer!!.keyboardSwitch = this // todo get rid of !!
    }

    override fun onBindInput() { // todo investigate called twice
        Log.d(LOG_TAG, "onBindInput()")
        kbLayoutInitializer?.ic = currentInputConnection
    }

    override fun onCreateInputView(): View {
        Log.d(LOG_TAG, "onCreateInputView()")
        kbLayoutInitializer?.ic = currentInputConnection
        return kbLayoutInitializer!!.initKeyboard()!!
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInputView()")
        if(currentInputConnection != null) {
            kbLayoutInitializer?.ic = currentInputConnection
        }
        if (info != null) {
            kbLayoutInitializer?.currentInputEditorInfo = info
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