package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.support.annotation.LayoutRes
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
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

    private lateinit var kv: KeyboardView
    private lateinit var keyboardQwerty: Keyboard
    private lateinit var keyboardSa: Keyboard
    private lateinit var layout: View
    private var isCaps: Boolean = false
    private var keyPopupHeight: Int = 0
    private var keyPopupWidth: Int = 0

    override fun onCreateInputView(): View {
        initRes(this) // essential !!!

        layout = layoutInflater.inflate(R.layout.keyboard, null)
        kv = layout.findViewById(R.id.keyboard) as KeyboardView
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
        kv.keyboard.keys[0].label= keyboardLang // set "Switch" key label to the language
        return layout
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
            Keycode.SWITCH_KEYBOARD.code -> {
                showPopup(this, layout, R.layout.keyboard_switch, getKeyRect(Keycode.SWITCH_KEYBOARD.code))
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
        kv.isPreviewEnabled = !(primaryCode == Keycode.SWITCH_KEYBOARD.code
                || primaryCode == Keycode.DONE.code
                || primaryCode == Keycode.QWERTY_SYM.code
                || primaryCode == Keycode.SANSKRIT_NUM.code
                || primaryCode == Keycode.SPACE.code
                || primaryCode == Keycode.SHIFT.code
                || primaryCode == Keycode.DELETE.code)
    }

    override fun onRelease(primaryCode: Int) {
        // release SHIFT after a char pressed
        if(isCaps && !(primaryCode == Keycode.SWITCH_KEYBOARD.code
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

    private fun showPopup(context: Context, parent: View, @LayoutRes layout: Int, rect: IntArray) {
        val popup = PopupWindow(context)

        popup.contentView = layoutInflater.inflate(layout, null)
        popup.isOutsideTouchable = true
        val radioQwerty = popup.contentView.findViewById<RadioButton>(R.id.rdEnglish)
        val radioSa = popup.contentView.findViewById<RadioButton>(R.id.rdSanskrit)

        when(PreferenceHelper(this).getKeyboardLang()) {
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
                else -> { /* nothing */ }
            }
        }
        popup.showAtLocation(parent, Gravity.START and Gravity.TOP, rect[2]/4, rect[3] + 3)
        popup.update(keyPopupWidth, keyPopupHeight)
    }

    private fun setKeyboard(kb: KeyboardLang, save: Boolean = true) {
        when(kb) {
            KeyboardLang.QWERTY -> {
                kv.keyboard = keyboardQwerty
                kv.keyboard.keys[0].label= KeyboardLang.QWERTY.lang
            }
            KeyboardLang.SA -> {
                kv.keyboard = keyboardSa
                kv.keyboard.keys[0].label= KeyboardLang.SA.lang
            }
            else -> {
                kv.keyboard = keyboardQwerty
                kv.keyboard.keys[0].label= KeyboardLang.QWERTY.lang
            }
        }
        kv.invalidateAllKeys()
        if(save) {
            PreferenceHelper(this).setKeyboardLang(kb.lang)
        }
    }

    /**
     * Returns [x relative to screen, y relative to screen, width, height] of a key
     */
    private fun getKeyRect(keyCode: Int): IntArray {
        val key = kv.keyboard.keys.first {
            it.codes.contains(keyCode)
        }
        val keyboardCoords = IntArray(2)
        layout.getLocationOnScreen(keyboardCoords)
        return intArrayOf(keyboardCoords[0] + key.x, keyboardCoords[1] + key.y, key.width, key.height)
    }

    private fun playClick(keyCode: Int) {
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (keyCode) {
            32 -> am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            Keyboard.KEYCODE_DONE, 10 -> am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN)
            Keyboard.KEYCODE_DELETE -> am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            else -> am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
    }

    private fun initRes(context: Context) {
        // strings
        KeyboardLang.QWERTY.lang = context.getString(R.string.kbQwerty)
        KeyboardLang.SA.lang = context.getString(R.string.kbSanskrit)

        // integers
        Keycode.DELETE.code = context.resources.getInteger(R.integer.keycode_backspace)
        Keycode.DONE.code = context.resources.getInteger(R.integer.keycode_ac)
        Keycode.SHIFT.code = context.resources.getInteger(R.integer.keycode_shift)
        Keycode.QWERTY_SYM.code = context.resources.getInteger(R.integer.keycode_qwerty_sym)
        Keycode.SANSKRIT_NUM.code = context.resources.getInteger(R.integer.keycode_sa_dg)
        Keycode.SWITCH_KEYBOARD.code = context.resources.getInteger(R.integer.keycode_switch)
        Keycode.SPACE.code = context.resources.getInteger(R.integer.keycode_space)

        keyPopupWidth = context.resources.getInteger(R.integer.size_key_popup_width) // todo: investigate
        keyPopupHeight = context.resources.getInteger(R.integer.size_key_popup_height)
    }

    companion object {
        // debug
        private const val LOG_SWITCH_POPUP = "Switch pop-up"
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
        SWITCH_KEYBOARD(0),
        SPACE(0)
    }
}



