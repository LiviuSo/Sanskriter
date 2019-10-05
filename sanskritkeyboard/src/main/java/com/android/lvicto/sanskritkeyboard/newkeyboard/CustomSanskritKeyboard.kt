package com.android.lvicto.sanskritkeyboard.newkeyboard

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType
import com.android.lvicto.sanskritkeyboard.newkeyboard.component.ServiceWrapper
import com.android.lvicto.sanskritkeyboard.newkeyboard.component.SwitchKeyboardReceiver
import com.android.lvicto.sanskritkeyboard.service.SanskritCustomKeyboard.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.utils.getOrientation
import com.android.lvicto.sanskritkeyboard.utils.isTablet

class CustomSanskritKeyboard : InputMethodService() {

    private var update: Boolean = false
    private var currentConfig: KeyboardConfig = KeyboardConfig()
    private lateinit var icw: ServiceWrapper
    private var keyboard: SanskritKeyboard? = null
    private var broadcastReceiver: SwitchKeyboardReceiver? = null
    private var currentKbType: KeyboardType = KeyboardType.QWERTY

    override fun onCreateInputMethodSessionInterface(): AbstractInputMethodSessionImpl {
        Log.d(LOG_TAG, "onCreateInputMethodSessionInterface()")

        // create keyboard
        createKeyboard()

        return super.onCreateInputMethodSessionInterface()
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInput()")

        // register broadcast for switching keyboard
        registerBroadcast()

        // toggle update on/off
        val config = getKeyboardConfig()
        update = if(config != currentConfig) {
            currentConfig = config
            true
        } else {
            false
        }

        // rebind to keyboard
        rebindToKeyboard()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        if(update) {
            // show keyboard
            showKeyboard()
            update = false
        }
        keyboard?.setActionIcon()

        Log.d(LOG_TAG, "onStartInputView(): $update")
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        Log.d(LOG_TAG, "onUpdateSelection()")
        icw.updateSelection(newSelStart, newSelEnd, oldSelStart, oldSelEnd)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(LOG_TAG, "onConfigurationChanged()")
        // nothing to do; work will be done in onStartInput() and onStartInputView()
    }

    override fun onFinishInput() {
        Log.d(LOG_TAG, "onFinishInput()")
        // unregister receiver
        unregisterBroadcast()
    }

    fun switchKeyboard() { // create a new keyboard
        Log.d(LOG_TAG, "switchKeyboard()")
        // create keyboard
        createKeyboard()

        // bind to keyboard
        rebindToKeyboard()

        currentConfig = getKeyboardConfig(true)

        // show keyboard
        showKeyboard()

        keyboard?.setActionIcon()
    }

    private fun showKeyboard() {
        keyboard?.initKeyboardUtils(currentConfig)
        setInputView(keyboard?.buildKeyboard())
    }

    private fun unregisterBroadcast() {
        unregisterReceiver(broadcastReceiver)
        broadcastReceiver = null
    }

    private fun rebindToKeyboard() {
        icw = ServiceWrapper(currentInputConnection, currentInputEditorInfo)
        keyboard?.bindIms(icw)
    }

    private fun registerBroadcast() {
        broadcastReceiver = SwitchKeyboardReceiver(this)
        registerReceiver(broadcastReceiver, SwitchKeyboardReceiver.intentFilter)
    }

    private fun createKeyboard() {
        keyboard = null
        keyboard = SanskritKeyboard(context = applicationContext)
    }

    private fun getKeyboardConfig(swapping: Boolean = false): KeyboardConfig {
        val isTablet = applicationContext.isTablet()
        val orientation = applicationContext.getOrientation()
        currentKbType =  if(swapping) {
            swapCurrentKeyboardType()
        } else {
            getKeyboardTypeFromActionLabel()
        }
        return KeyboardConfig(isTablet, orientation, currentKbType)
    }

    private fun getKeyboardTypeFromActionLabel(): KeyboardType = when (currentInputEditorInfo.actionLabel) {
        applicationContext.getString(R.string.kbHintSanskrit) -> {
            KeyboardType.SA
        }
        applicationContext.getString(R.string.kbHintIAST) -> {
            KeyboardType.QWERTY
        }
        else -> { // default to Sanskrit
            KeyboardType.SA
        }
    }

    private fun swapCurrentKeyboardType(): KeyboardType = if (currentKbType == KeyboardType.QWERTY) {
            KeyboardType.SA
        } else {
            KeyboardType.QWERTY
        }
}