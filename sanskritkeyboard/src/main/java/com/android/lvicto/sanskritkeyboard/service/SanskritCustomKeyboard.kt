package com.android.lvicto.sanskritkeyboard.service

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.keyboard.KbLayoutInitializer
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType
import com.android.lvicto.sanskritkeyboard.utils.getOrientation
import com.android.lvicto.sanskritkeyboard.utils.isTablet

class SanskritCustomKeyboard : InputMethodService(), KeyboardSwitch {

    private var mIsTablet = true
    private var mOrientation = Configuration.ORIENTATION_UNDEFINED
    private var mCurrentKbType = KeyboardType.QWERTY

    override fun switchKeyboard() {
        Log.d(LOG_TAG, "switchKeyboard()")
        val kbType = if (mCurrentKbType == KeyboardType.QWERTY) {
            KeyboardType.SA
        } else {
            KeyboardType.QWERTY
        }
        updateConfigParams(kbType)
        Log.d(LOG_TAG, "after: $mCurrentKbType")
        val kbConfig = KeyboardConfig(mIsTablet, mOrientation, mCurrentKbType)
        kbLayoutInitializer = KbLayoutInitializer.getLayoutInitializer(applicationContext, kbConfig)
        kbLayoutInitializer.keyboardSwitch = this // because we need the inputConnection
        setInputView(kbLayoutInitializer.initKeyboardView())
        completeLayoutInit(currentInputEditorInfo)
    }

    private lateinit var kbLayoutInitializer: KbLayoutInitializer


    override fun onCreateInputMethodSessionInterface(): AbstractInputMethodSessionImpl {
        Log.d(LOG_TAG, "onCreateInputMethodSessionInterface()")
        return super.onCreateInputMethodSessionInterface()
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInput()")
    }

    override fun onExtractedSelectionChanged(start: Int, end: Int) {
        Log.d(LOG_TAG, "onExtractedSelectionChanged()")
        super.onExtractedSelectionChanged(start, end)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(LOG_TAG, "onConfigurationChanged()")
        super.onConfigurationChanged(newConfig)
    }

    override fun onBindInput() {
        Log.d(LOG_TAG, "onBindInput()")
        super.onBindInput()
    }

    override fun onUnbindInput() {
        Log.d(LOG_TAG, "onUnbindInput()")
        super.onUnbindInput()
    }

    override fun onFinishInput() {
        Log.d(LOG_TAG, "onFinishInput()")
        super.onFinishInput()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInputView($info $restarting)")
        // perform auto-switch if necessary
        val currentKbType = when (currentInputEditorInfo.actionLabel) {
            applicationContext.getString(R.string.kbHintSanskrit) -> {
                KeyboardType.SA
            }
            applicationContext.getString(R.string.kbHintIAST) -> {
                KeyboardType.QWERTY
            }
            else -> { // if the editView don't have a label matching "San" or "IAST", leave it as it is
                mCurrentKbType
            }
        }
        if (updateConfigParams(currentKbType)) { // if conf changed, create a new initializer
            val kbConfig = KeyboardConfig(mIsTablet, mOrientation, mCurrentKbType)
            kbLayoutInitializer = KbLayoutInitializer.getLayoutInitializer(applicationContext, kbConfig)
            kbLayoutInitializer.keyboardSwitch = this // because we need the inputConnection
            setInputView(kbLayoutInitializer.initKeyboardView())
        }
        completeLayoutInit(info)
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        // update suggestions
        kbLayoutInitializer.updateSuggestionsOnSelection()
        // set the selection
        kbLayoutInitializer.setSelection(newSelStart, newSelEnd, oldSelStart)
    }

    private fun completeLayoutInit(info: EditorInfo?) {
        if (currentInputConnection != null) {
            kbLayoutInitializer.ic = currentInputConnection
            // set first letter allCaps
            kbLayoutInitializer.toggleAllCapsFirstLetter()
        }
        if (info != null) {
            // simply update the action button
            kbLayoutInitializer.currentInputEditorInfo = info
            kbLayoutInitializer.setActionType()
        }
    }

    private fun updateConfigParams(currentKbType: KeyboardType): Boolean {
        val newIsTablet = applicationContext.isTablet()
        val newOrientation = applicationContext.getOrientation()
        val updated = mIsTablet != newIsTablet
                || mOrientation != newOrientation
                || mCurrentKbType != currentKbType
        if (updated) {
            mIsTablet = newIsTablet
            mOrientation = newOrientation
            mCurrentKbType = currentKbType
        }
        return updated
    }

    companion object {
        const val LOG_TAG = "SanskritCustomKeyboard"
    }
}

interface KeyboardSwitch {
    fun switchKeyboard()
}
