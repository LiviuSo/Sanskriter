package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
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
    private lateinit var keyboard: Keyboard
    private lateinit var layout: View

    private var isCaps: Boolean = false

    override fun onCreateInputView(): View {
        layout = layoutInflater.inflate(R.layout.keyboard, null)
        kv = layout.findViewById(R.id.keyboard) as KeyboardView
        keyboard = Keyboard(this, R.xml.qwerty_en)
        kv.keyboard = keyboard
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
                keyboard.isShifted = isCaps
                kv.invalidateAllKeys()
            }
            Keyboard.KEYCODE_DONE -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
            KEY_SHIFT_KEYBOARD -> { // todo change the keyboard
                Toast.makeText(this, "Change keys", Toast.LENGTH_SHORT).show()
            }
            KEY_SHIFT_KEYBOARD1 -> {
                val coords = getKeyXY(KEY_SHIFT_KEYBOARD1)
                Toast.makeText(this, "Switch: ${coords.first} ${coords.second}", Toast.LENGTH_SHORT).show()
            }
            else -> {
                var code = primaryCode.toChar()
                if (isLetter(code) && isCaps) {
                    code = toUpperCase(code)
                }
                ic.commitText(valueOf(code), 1)
                val coords = getKeyXY(primaryCode)
                Toast.makeText(this, "Switch: ${coords.first} ${coords.second}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getKeyXY(keyCode: Int): Pair<Int, Int> {
        val key = kv.keyboard.keys.first {
            it.codes.contains(keyCode)
        }
        val keyboardCoords = IntArray(2)
        layout.getLocationOnScreen(keyboardCoords)
        return Pair(keyboardCoords[0] + key.x, keyboardCoords[1] + key.y)
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


    override fun swipeRight() {
    }

    override fun onPress(primaryCode: Int) {
        if (primaryCode == -2) {
            kv.isPreviewEnabled = false
        }
    }

    override fun onRelease(primaryCode: Int) {
        if (primaryCode == -2) {
            kv.isPreviewEnabled = true
        }
    }

    override fun swipeLeft() {
    }

    override fun swipeUp() {
    }

    override fun swipeDown() {
    }

    override fun onText(text: CharSequence?) {
    }

    companion object {
        private const val KEY_SHIFT_KEYBOARD = 9999
        private const val KEY_SHIFT_KEYBOARD1 = -2
    }

}
