package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import androidx.annotation.IdRes

class CustomKeyboard2 : InputMethodService() {

    private var keyboardLayoutInitializer: KeyboardLayoutInitializer? = null

    override fun onInitializeInterface() {
        Log.d(LOG_TAG, "onInitializeInterface()")
        val kbConfig = KeyboardConfig(applicationContext.isTablet(), applicationContext.getOrientation(), KeyboardType.SA)
        keyboardLayoutInitializer = KeyboardLayoutInitializer.getLayoutInitializer(applicationContext, kbConfig)!!
    }

    override fun onBindInput() { // todo investigate called twice
        Log.d(LOG_TAG, "onBindInput()")
        keyboardLayoutInitializer?.ic = currentInputConnection
    }

    override fun onCreateInputView(): View {
        Log.d(LOG_TAG, "onCreateInputView()")
        return keyboardLayoutInitializer!!.initKeyboard()!!
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInputView()")
        keyboardLayoutInitializer?.ic = currentInputConnection
        if (info != null) {
            keyboardLayoutInitializer?.currentInputEditorInfo = info
        }
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

fun Context.layoutInflater(): LayoutInflater =
        this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

infix fun View.button(@IdRes id: Int): Button = (this.findViewById(id) as Button)