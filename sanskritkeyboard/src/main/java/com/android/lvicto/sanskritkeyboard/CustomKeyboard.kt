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

    override fun swipeRight() {
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

    override fun swipeLeft() {
    }

    override fun swipeUp() {
    }

    override fun swipeDown() {
    }

    override fun onText(text: CharSequence?) {
    }

    private fun showPopup(context: Context, parent: View, @LayoutRes layout: Int, rect: IntArray) {
        // Toast.makeText(this, "Window pop-up rect: ${rect.top} ${rect.bottom}", Toast.LENGTH_SHORT).show()
        val popup = PopupWindow(context)
        popup.contentView = layoutInflater.inflate(layout, null)
        popup.isOutsideTouchable = true
        popup.setOnDismissListener {
            Toast.makeText(this, "Switch keys", Toast.LENGTH_SHORT).show()
        }
        popup.showAtLocation(parent, Gravity.START and Gravity.TOP, rect[2]/4, rect[3] + 3)
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

}
