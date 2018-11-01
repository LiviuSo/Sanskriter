package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.RadioButton
import com.android.lvicto.sanskritkeyboard.utils.PreferenceHelper
import java.lang.Character.isLetter
import java.lang.Character.toUpperCase
import java.lang.String.valueOf

/**
 * Expose intents to change the locales programmatically
 * Must include Sanskrit, IAST, Romana, English
 *
 */
class CustomKeyboard : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private var layout: View? = null
    private lateinit var kv: KeyboardView
    private lateinit var keyboardQwerty: Keyboard
    private lateinit var keyboardSa: Keyboard
    private var isCaps: Boolean = false
    private var keyPopupHeight: Int = 0
    private var keyPopupWidth: Int = 0

    private val LOG_TAG = "CustomKeyboard"

    private var switch: Boolean = false
    private val autoSwitch = true // todo make it a setting

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        initRes(this) // essential !!!
        switch = false

        if (autoSwitch) {
            Log.d(LOG_TAG, "onStartInput")
            var currentLang = PreferenceHelper(this).getKeyboardLang()
            val saveCurrentLang = currentLang
            if (attribute?.hintText == this.getString(R.string.kbSanskrit)) {
                if (currentLang != this.getString(R.string.kbSanskrit)) {
                    switch = true
                    currentLang = this.getString(R.string.kbSanskrit)
                    Log.d(LOG_TAG, "onStartInput: switch $currentLang")
                }
            } else if (attribute?.hintText == this.getString(R.string.kbLatin)) {
                if (currentLang != this.getString(R.string.kbLatin)) {
                    currentLang = this.getString(R.string.kbLatin)
                    switch = true
                    Log.d(LOG_TAG, "onStartInput: switch $currentLang")
                }
            } else {
                switch = false // todo investigate
            }
            if (switch && layout != null) {
                Log.d(LOG_TAG, "onStartInput: switch && layout != null")
                kv.keyboard = when (currentLang) {
                    KeyboardLang.QWERTY.lang -> keyboardQwerty
                    KeyboardLang.SA.lang -> keyboardSa
                    else -> {
                        keyboardQwerty
                    }
                }
                setLangLabelToSwitchKbKey(currentLang)
            } else if(!switch) {
                currentLang = saveCurrentLang // restore prev lang
            }
            PreferenceHelper(this).setKeyboardLang(currentLang)
        }
    }

    override fun onCreateInputView(): View {
        Log.d(LOG_TAG, "onCreateInputView()")
        if (layout == null) {
            Log.d(LOG_TAG, "onCreateInputView: layout == null")
            layout = layoutInflater.inflate(R.layout.keyboard, null)
            kv = layout?.findViewById(R.id.keyboard) as KeyboardView
            keyboardQwerty = Keyboard(this, R.xml.keyboard_qwerty)
            keyboardSa = Keyboard(this, R.xml.keyboard_sa)

            var keyboardLang = PreferenceHelper(this).getKeyboardLang()
            kv.keyboard = when (keyboardLang) {
                KeyboardLang.QWERTY.lang -> keyboardQwerty
                KeyboardLang.SA.lang -> keyboardSa
                else -> {
                    keyboardLang = KeyboardLang.QWERTY.lang
                    keyboardQwerty
                }
            }
            kv.setOnKeyboardActionListener(this)
            setLangLabelToSwitchKbKey(keyboardLang)
        }
        return layout as View
    }

    private fun setLangLabelToSwitchKbKey(keyboardLang: String) { // todo refactor to other class + unit test
        getKeyWithCode(Keycode.KB.code).label = keyboardLang
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection
        playClick(keyCode = primaryCode)
        when (primaryCode) {
            Keycode.DELETE.code ->
                ic.deleteSurroundingText(1, 0)
            Keycode.SHIFT.code -> {
                isCaps = !isCaps
                keyboardQwerty.isShifted = isCaps
                kv.invalidateAllKeys()
            }
            Keycode.DONE.code -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
            Keycode.KB.code -> {
                showPopup(this, this.layout!!, R.layout.keyboard_switch, getKeyRect(primaryCode))
            }
            Keycode.QWERTY_SYM.code -> {
                // ignore // todo: action on tap instead of long tap
            }
            else -> {
                var code = primaryCode.toChar()
                if (isLetter(code) && isCaps) {
                    code = toUpperCase(code)
                }
                ic.commitText(valueOf(code), 1)
            }
        }
    }

    override fun onPress(primaryCode: Int) {
        kv.isPreviewEnabled = !(primaryCode == Keycode.KB.code
                || primaryCode == Keycode.DONE.code
                || primaryCode == Keycode.QWERTY_SYM.code
                || primaryCode == Keycode.SANSKRIT_NUM.code
                || primaryCode == Keycode.SPACE.code
                || primaryCode == Keycode.SHIFT.code
                || primaryCode == Keycode.DELETE.code)
    }

    override fun onRelease(primaryCode: Int) {
        // release SHIFT after a char pressed
        if (isCaps && !(primaryCode == Keycode.KB.code
                        || primaryCode == Keycode.DONE.code
                        || primaryCode == Keycode.QWERTY_SYM.code
                        || primaryCode == Keycode.SANSKRIT_NUM.code
                        || primaryCode == Keycode.SPACE.code
                        || primaryCode == Keycode.SHIFT.code
                        || primaryCode == Keycode.DELETE.code)) {
            isCaps = false
            keyboardQwerty.isShifted = isCaps
            kv.invalidateAllKeys()
        }
    }

    // todo use logs to figure out an answer to https://stackoverflow.com/questions/47098170/android-custom-keyboard-popup
    override fun swipeRight() {
        // todo Log
    }

    override fun swipeLeft() {
        // todo Log
    }

    override fun swipeUp() {
        // todo Log
    }

    override fun swipeDown() {
        // todo Log
    }

    override fun onText(text: CharSequence?) {
        // todo Log
    }

    // todo https://stackoverflow.com/questions/8378012/detect-long-press-on-virtual-back-key for long press
    // with KeyEvent.Callback

    private fun showPopup(context: Context, parent: View, @LayoutRes layout: Int, rect: IntArray) { // todo refactor to separate class + UI test
        val popup = PopupWindow(context)

        popup.contentView = layoutInflater.inflate(layout, null)
        popup.isOutsideTouchable = true
        val radioQwerty = popup.contentView.findViewById<RadioButton>(R.id.rdEnglish)
        val radioSa = popup.contentView.findViewById<RadioButton>(R.id.rdSanskrit)

        when (PreferenceHelper(this).getKeyboardLang()) {
            KeyboardLang.QWERTY.lang -> radioQwerty.isChecked = true
            KeyboardLang.SA.lang -> radioSa.isChecked = true
            else -> {
                radioQwerty.isChecked = true // default QWERTY
            }
        }
        radioQwerty.setOnClickListener {
            popup.dismiss()
        }
        radioSa.setOnClickListener {
            popup.dismiss()
        }
        popup.setOnDismissListener {
            when {
                radioQwerty.isChecked -> {
                    setKeyboard(KeyboardLang.QWERTY)
                }
                radioSa.isChecked -> {
                    setKeyboard(KeyboardLang.SA)
                }
                else -> { /* nothing */
                }
            }
        }
        popup.showAtLocation(parent, Gravity.END and Gravity.BOTTOM, rect[0] + rect[2], rect[1] - keyPopupHeight) // todo generalize
        popup.update(keyPopupWidth, keyPopupHeight)
    }

    private fun setKeyboard(kb: KeyboardLang, save: Boolean = true) {
        var currentLang = KeyboardLang.QWERTY.lang
        when (kb) {
            KeyboardLang.SA -> {
                kv.keyboard = keyboardSa
                currentLang = KeyboardLang.SA.lang
            }
            KeyboardLang.QWERTY -> {
                kv.keyboard = keyboardQwerty
            }
            else -> {
                kv.keyboard = keyboardQwerty
            }
        }
        setLangLabelToSwitchKbKey(currentLang)
        kv.invalidateAllKeys()
        if (save) {
            PreferenceHelper(this).setKeyboardLang(kb.lang)
        }
    }

    /**
     * Returns [x relative to screen, y relative to screen, width, height] of a key
     */
    private fun getKeyRect(keyCode: Int): IntArray {
        val key = getKeyWithCode(keyCode)
        val keyboardCoords = IntArray(2)
        kv.getLocationOnScreen(keyboardCoords)
        val rect = intArrayOf(key.x, key.y, key.width, key.height)
        Log.d(LOG_TAG, "root view: ${kv.rootView.x}, ${kv.rootView.y}, ${kv.rootView.width}, ${kv.rootView.height}")
        Log.d(LOG_TAG, "kv: ${kv.x}, ${kv.y}, ${kv.width}, ${kv.height}")
        Log.d(LOG_TAG, "key: ${rect[0]}, ${rect[1]}, ${rect[2]}, ${rect[3]}")
        return rect
    }

    private fun getKeyWithCode(keyCode: Int): Keyboard.Key {
        return kv.keyboard.keys.first {
            it.codes.contains(keyCode)
        }
    }

    private fun playClick(keyCode: Int) {
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (keyCode) {
            Keycode.SPACE.code -> am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            Keyboard.KEYCODE_DONE, 10 -> am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN)
            Keyboard.KEYCODE_DELETE -> am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            else -> am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
    }

    private fun initRes(context: Context) {
        // strings
        KeyboardLang.QWERTY.lang = context.getString(R.string.kbLatin)
        KeyboardLang.SA.lang = context.getString(R.string.kbSanskrit)

        // integers
        Keycode.DELETE.code = context.resources.getInteger(R.integer.keycode_backspace)
        Keycode.DONE.code = context.resources.getInteger(R.integer.keycode_ac)
        Keycode.SHIFT.code = context.resources.getInteger(R.integer.keycode_shift)
        Keycode.QWERTY_SYM.code = context.resources.getInteger(R.integer.keycode_qwerty_sym)
        Keycode.SANSKRIT_NUM.code = context.resources.getInteger(R.integer.keycode_sa_dg)
        Keycode.SPACE.code = context.resources.getInteger(R.integer.keycode_space)
        Keycode.KB.code = context.resources.getInteger(R.integer.keycode_switch)

        keyPopupWidth = context.resources.getInteger(R.integer.size_key_popup_width) // todo: investigate
        keyPopupHeight = context.resources.getInteger(R.integer.size_key_popup_height)
    }

    enum class KeyboardLang(var lang: String) {
        QWERTY(""),
        SA(""),
        NONE("")
    }

    enum class Keycode(var code: Int) {
        DELETE(0),
        DONE(0),
        SHIFT(0),
        QWERTY_SYM(0),
        SANSKRIT_NUM(0),
        SPACE(0),
        KB(0)
    }
}



