package com.android.lvicto.sanskritkeyboard.service

import android.content.res.Configuration
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.keyboard.KbLayoutInitializer
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType
import com.android.lvicto.sanskritkeyboard.utils.getOrientation
import com.android.lvicto.sanskritkeyboard.utils.isTablet

// todo fix bug : long tap to switch keyboard, then press Action button -> crash
class SanskritCustomKeyboard : StubbedInputMethodService(), KeyboardSwitch {

    private var mIsTablet = true
    private var mOrientation = Configuration.ORIENTATION_UNDEFINED
    private var mCurrentKbType = KeyboardType.NONE
    private var mNewConfig = false
    private var mSwitching = false

    override fun switchKeyboard() {
        mCurrentKbType = if (mCurrentKbType == KeyboardType.QWERTY) {
            KeyboardType.SA
        } else {
            KeyboardType.QWERTY
        }
        Log.d(LOG_TAG, "switchKeyboard()")
        setInputView(kbLayoutInitializer.initKeyboard())
    }

    private lateinit var kbLayoutInitializer: KbLayoutInitializer

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInput($attribute $restarting) label: ${attribute?.actionLabel}")

        mNewConfig = if (!mSwitching) {
            val currentKbType = if (attribute?.actionLabel == applicationContext.getString(R.string.kbHintSanskrit)) {
                KeyboardType.SA
            } else {
                KeyboardType.QWERTY
            }
            updateConfigParams(currentKbType)
        } else {
            true // switching keyboard, so new config obviously
        }

        Log.d(LOG_TAG, "onStartInput() mNewConfig = $mNewConfig")
        if (mNewConfig) { // if new config, create a new initializer
            Log.d(LOG_TAG, "onStartInput() new kbLayoutInitializer:$mCurrentKbType")
            val kbConfig = KeyboardConfig(mIsTablet, mOrientation, mCurrentKbType)
            kbLayoutInitializer = KbLayoutInitializer.getLayoutInitializer(applicationContext, kbConfig)
            kbLayoutInitializer.keyboardSwitch = this // because we need the inputConnection
            kbLayoutInitializer.ic = currentInputConnection
            if (!mSwitching) { // the view will be created in switchKeyboard()
                Log.d(LOG_TAG, "onStartInput() mSwitching = $mSwitching")
                setInputView(kbLayoutInitializer.initKeyboard())
                completeLayoutInit(attribute)
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInputView($info $restarting)")
        completeLayoutInit(info)
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        Log.d(LOG_TAG, "onUpdateSelection: $oldSelStart $oldSelEnd $newSelStart $newSelEnd")
        // update suggestions
        val word = kbLayoutInitializer.getSurroundingWord()
        Log.d(LOG_TAG, "onUpdateSelection: $word")
        kbLayoutInitializer.updateSuggestions(word)
    }

    private fun completeLayoutInit(info: EditorInfo?) {
        if(currentInputConnection != null) {
            kbLayoutInitializer.ic = currentInputConnection
        }
        if(info != null) {
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
