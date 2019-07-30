package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class CustomKeyboard2 : InputMethodService() {

    private var keyboardLayoutInitializer: KeyboardLayoutInitializer? = null
    private lateinit var ic: InputConnection

    override fun onInitializeInterface() {
        Log.d(LOG_TAG, "onInitializeInterface()")
        val kbConfig = KeyboardConfig(applicationContext.isTablet(), applicationContext.getOrientation(), KeyboardType.QWERTY)
        keyboardLayoutInitializer = KeyboardLayoutInitializer.getLyoutInitializer(applicationContext, kbConfig)!!
    }

    override fun onBindInput() {
        super.onBindInput()
        Log.d(LOG_TAG, "onBindInput()")
        keyboardLayoutInitializer?.ic = currentInputBinding.connection
    }

    override fun onCreateInputView(): View {
        Log.d(LOG_TAG, "onCreateInputView()")
        return keyboardLayoutInitializer!!.initKeyboard()!!
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInputView()")
        keyboardLayoutInitializer?.ic = currentInputBinding.connection
    }

    companion object {
        const val LOG_TAG = "CustomKeyboard2"
    }
}

fun Context.getOrientation(): Int = this.resources?.configuration?.orientation!!

fun Context.isTablet(): Boolean {
    val xlarge = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == 4
    val large = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
    return xlarge || large
}

fun Int.getVal(context: Context): Int = context.resources.getInteger(this)