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
    private lateinit var keyboardEn: Keyboard
    private lateinit var keyboardRo: Keyboard
    private lateinit var keyboardSa: Keyboard
    private lateinit var keyboardSaIAST: Keyboard
    private lateinit var layout: View

    private var isCaps: Boolean = false

    override fun onCreateInputView(): View {
        layout = layoutInflater.inflate(R.layout.keyboard, null)
        kv = layout.findViewById(R.id.keyboard) as KeyboardView
        keyboardEn = Keyboard(this, R.xml.keyboard_en)
        keyboardRo = Keyboard(this, R.xml.keyboard_ro)
        keyboardSa = Keyboard(this, R.xml.keyboard_sa)
        keyboardSaIAST = Keyboard(this, R.xml.keyboard_sa_iast)
        val keyboardLang = PreferenceHelper(this).getKeyboardLang()
        kv.keyboard = when (keyboardLang) {
            "en" -> keyboardEn // todo make string constants
            "ro" -> keyboardRo
            "sa" -> keyboardSa
            "iast" -> keyboardSaIAST
            else -> keyboardEn
        }
        kv.setOnKeyboardActionListener(this)
        return layout
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection
        playClick(keyCode = primaryCode)
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE ->
                ic.deleteSurroundingText(1, 0)
            Keyboard.KEYCODE_SHIFT -> {
                isCaps = !isCaps
                keyboardEn.isShifted = isCaps
                kv.invalidateAllKeys()
            }
            Keyboard.KEYCODE_DONE -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
            KEY_SWITCH_KEYBOARD -> {
                showPopup(this, layout, R.layout.keyboard_switch, getKeyRect(KEY_SWITCH_KEYBOARD))
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
        if (primaryCode == KEY_SWITCH_KEYBOARD) {
            kv.isPreviewEnabled = false
        }
    }

    override fun onRelease(primaryCode: Int) {
        if (primaryCode == KEY_SWITCH_KEYBOARD) {
            kv.isPreviewEnabled = true
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
        // Toast.makeText(this, "Window pop-up rect: ${rect.top} ${rect.bottom}", Toast.LENGTH_SHORT).show()
        val popup = PopupWindow(context)
        popup.contentView = layoutInflater.inflate(layout, null)
        popup.isOutsideTouchable = true
        val radioEn = popup.contentView.findViewById<RadioButton>(R.id.rdEnglish)
        val radioRo = popup.contentView.findViewById<RadioButton>(R.id.rdRomana)
        val radioSa = popup.contentView.findViewById<RadioButton>(R.id.rdSanskrit)
        val radioSaIAST = popup.contentView.findViewById<RadioButton>(R.id.rdIAST)

        val lang = when(PreferenceHelper(this).getKeyboardLang()) {
            "en" -> KeyboardLang.EN
            "ro" -> KeyboardLang.RO
            "sa" -> KeyboardLang.SA
            "iast" -> KeyboardLang.IAST
            else -> KeyboardLang.NONE
        }
        when(lang) {
            KeyboardLang.EN -> radioEn.isChecked = true
            KeyboardLang.RO -> radioRo.isChecked = true
            KeyboardLang.SA -> radioSa.isChecked = true
            KeyboardLang.IAST -> radioSaIAST.isChecked = true
            KeyboardLang.NONE -> radioEn.isChecked = true
        }
        radioEn.setOnClickListener {
            popup.dismiss()
        }
        radioRo.setOnClickListener {
            popup.dismiss()
        }
        radioSa.setOnClickListener {
            popup.dismiss()
        }
        radioSaIAST.setOnClickListener {
            popup.dismiss()
        }

        popup.setOnDismissListener {
            when {
                radioEn.isChecked -> {
                    setKeyboard(KeyboardLang.EN)
                }
                radioRo.isChecked -> {
                    setKeyboard(KeyboardLang.RO)
                }
                radioSa.isChecked -> {
                    setKeyboard(KeyboardLang.SA)
                }
                radioSaIAST.isChecked -> {
                    setKeyboard(KeyboardLang.IAST)
                }
                else -> {
                    // nothing
                }
            }
        }
        popup.showAtLocation(parent, Gravity.START and Gravity.TOP, rect[2]/4, rect[3] + 3)
    }

    private fun setKeyboard(kb: KeyboardLang, save: Boolean = true) {
        when(kb) {
            KeyboardLang.EN -> {
                kv.keyboard = keyboardEn
            }
            KeyboardLang.RO -> {
                kv.keyboard = keyboardRo
            }
            KeyboardLang.SA -> {
                kv.keyboard = keyboardSa
            }
            KeyboardLang.IAST -> {
                kv.keyboard = keyboardSaIAST
            }
            else -> {
                kv.keyboard = keyboardEn
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

    companion object {
        private const val KEY_SWITCH_KEYBOARD = -2
    }

    enum class KeyboardLang(val lang: String) {
        EN("en"),
        RO("ro"),
        SA("sa"),
        IAST("iast"),
        NONE("none")
    }
}