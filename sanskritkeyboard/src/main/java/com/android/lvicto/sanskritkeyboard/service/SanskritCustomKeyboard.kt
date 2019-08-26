package com.android.lvicto.sanskritkeyboard.service

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.IdRes
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.keyboard.KbLayoutInitializer
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType


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
        mSwitching = true
        onStartInput(null, false)
        mSwitching = false
        setInputView(kbLayoutInitializer.initKeyboard())
    }

    private lateinit var kbLayoutInitializer: KbLayoutInitializer

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInput($attribute $restarting) label: ${attribute?.actionLabel}")

        mNewConfig = if (!mSwitching) { //
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
        kbLayoutInitializer.ic = currentInputConnection
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
