package com.android.lvicto.sanskritkeyboard.service

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.android.lvicto.sanskritkeyboard.R
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
    private lateinit var keyboardQwertySym: Keyboard
    private lateinit var keyboardSaSym: Keyboard
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
        setCandidatesViewShown(false)

        if (autoSwitch) {
            Log.d(LOG_TAG, "onStartInput")
            var currentLang = PreferenceHelper(this).getKeyboardLang()
            val saveCurrentLang = currentLang
            if (attribute?.hintText == this.getString(R.string.kbHintSanskrit)) {
                if (currentLang != this.getString(R.string.kbLabelSanskrit)) {
                    switch = true
                    currentLang = this.getString(R.string.kbLabelSanskrit)
                    Log.d(LOG_TAG, "onStartInput: switch $currentLang")
                }
            } else if (attribute?.hintText == this.getString(R.string.kbHintIAST)
                        || attribute?.hintText == this.getString(R.string.kbHintEnglish)
                        || attribute?.hintText == this.getString(R.string.kbHintRomanian)) {
                if (currentLang != this.getString(R.string.kbLabelLatin)) {
                    currentLang = this.getString(R.string.kbLabelLatin)
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
            kv.isPreviewEnabled = false
            keyboardQwerty = Keyboard(this, R.xml.keyboard_qwerty)
            keyboardQwertySym = Keyboard(this, R.xml.keyboard_qwerty_sym)
            keyboardSa = Keyboard(this, R.xml.keyboard_sa)
            keyboardSaSym = Keyboard(this, R.xml.keyboard_sa_sym)

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
                val imeOptions = currentInputEditorInfo.imeOptions
                when {
                    imeOptions == 0 -> {
                        Log.d(LOG_TAG, " EditorInfo.IME_ACTION_UNSPECIFIED")
                    }
                    EditorInfo.IME_ACTION_PREVIOUS and imeOptions == EditorInfo.IME_ACTION_PREVIOUS -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_NAVIGATE_PREVIOUS))
                            Log.d(LOG_TAG, "EditorInfo.IME_ACTION_PREVIOUS")
                        } else {
                            Log.d(LOG_TAG, "EditorInfo.IME_ACTION_PREVIOUS unavailable")
                        }
                    }
                    EditorInfo.IME_ACTION_DONE and imeOptions == EditorInfo.IME_ACTION_DONE -> {
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                        Log.d(LOG_TAG, "EditorInfo.IME_ACTION_DONE")
                    }
                    EditorInfo.IME_ACTION_NEXT and imeOptions == EditorInfo.IME_ACTION_NEXT -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_NAVIGATE_NEXT))
                            Log.d(LOG_TAG, "EditorInfo.IME_ACTION_NEXT")
                        } else {
                            Log.d(LOG_TAG, "EditorInfo.IME_ACTION_NEXT unavailable")
                        }
                    }
                    EditorInfo.IME_ACTION_SEND and imeOptions == EditorInfo.IME_ACTION_SEND -> {
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                        Log.d(LOG_TAG, "EditorInfo.IME_ACTION_SEND")
                    }
                    EditorInfo.IME_ACTION_SEARCH and imeOptions == EditorInfo.IME_ACTION_SEARCH -> {
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, EditorInfo.IME_ACTION_SEARCH))
                        Log.d(LOG_TAG, "EditorInfo.IME_ACTION_SEARCH")
                    }
                    EditorInfo.IME_ACTION_GO and imeOptions == EditorInfo.IME_ACTION_GO -> {
                        ic.sendKeyEvent(KeyEvent(KeyEvent.KEYCODE_SEARCH, KeyEvent.KEYCODE_ENTER))
                        Log.d(LOG_TAG, "EditorInfo.IME_ACTION_GO")
                    }
                    EditorInfo.IME_ACTION_NONE and imeOptions == EditorInfo.IME_ACTION_NONE -> {
                        Log.d(LOG_TAG, "EditorInfo.IME_ACTION_NONE")
                    }
                    else -> {
                        Log.d(LOG_TAG, "EditorInfo.UNKNOWN")
                    }
                }
            }
            Keycode.KB.code -> {
                showPopup(this, this.layout!!, R.layout.keyboard_switch, getKeyRect(primaryCode))
            }
            Keycode.QWERTY_SYM.code -> {
                when {
                    kv.keyboard == keyboardQwerty -> kv.keyboard = keyboardQwertySym
                    kv.keyboard == keyboardSa -> kv.keyboard = keyboardSaSym
                    else -> {
                        // nothing
                    }
                }
            }
            Keycode.BACK.code -> {
                when {
                    kv.keyboard == keyboardQwertySym -> kv.keyboard = keyboardQwerty
                    kv.keyboard == keyboardSaSym -> kv.keyboard = keyboardSa
                    else -> {
                        // nothing
                    }
                }
            }
            else -> {
                var code = primaryCode.toChar()
                if (isLetter(code) && isCaps) {
                    code = toUpperCase(code)
                }
                ic.commitText(valueOf(code), 1)
                when {
                    kv.keyboard == keyboardQwertySym -> kv.keyboard = keyboardQwerty
                    kv.keyboard == keyboardSaSym -> kv.keyboard = keyboardSa
                    else -> {
                        // nothing
                    }
                }
                setPreview(this.layout!!.rootView, getKeyWithCode(primaryCode),
                        resources.getDimension(R.dimen.preview_width).toInt(),
                        resources.getDimension(R.dimen.preview_width).toInt(),
                        290 )
            }
        }
    }

    private val longPress = Runnable {
        Log.d(LOG_TAG, "Long press")
    }

    private val handler = Handler()

    override fun onPress(primaryCode: Int) {
        handler.postDelayed(longPress, 900)
        kv.isPreviewEnabled = !(primaryCode == Keycode.KB.code
                || primaryCode == Keycode.DONE.code
                || primaryCode == Keycode.QWERTY_SYM.code
                || primaryCode == Keycode.SANSKRIT_NUM.code
                || primaryCode == Keycode.SPACE.code
                || primaryCode == Keycode.SHIFT.code
                || primaryCode == Keycode.DELETE.code
                || primaryCode == Keycode.BACK.code)
        // detect long press
    }

    override fun onRelease(primaryCode: Int) {
        handler.removeCallbacks(longPress)
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
        val density = resources.displayMetrics.density // todo refactor to a method
        val scaledWidth = (keyPopupWidth * density).toInt()
        val scaledHeight = (keyPopupHeight * density).toInt()
        popup.showAtLocation(parent,
                Gravity.START and Gravity.TOP,
                rect[0],rect[1] - scaledWidth) // todo generalize
        if(Build.VERSION.SDK_INT < 23) {
            popup.update(scaledWidth, scaledHeight)
        }
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
        val rect = intArrayOf(key.x, key.y, key.width, key.height)
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
        KeyboardLang.QWERTY.lang = context.getString(R.string.kbLabelLatin)
        KeyboardLang.SA.lang = context.getString(R.string.kbLabelSanskrit)

        // integers
        Keycode.DELETE.code = context.resources.getInteger(R.integer.key_code_backspace)
        Keycode.DONE.code = context.resources.getInteger(R.integer.key_code_ac)
        Keycode.SHIFT.code = context.resources.getInteger(R.integer.key_code_shift)
        Keycode.QWERTY_SYM.code = context.resources.getInteger(R.integer.key_code_qwerty_sym)
        Keycode.SANSKRIT_NUM.code = context.resources.getInteger(R.integer.key_code_sa_dg)
        Keycode.SPACE.code = context.resources.getInteger(R.integer.key_code_space)
        Keycode.KB.code = context.resources.getInteger(R.integer.key_code_switch)
        Keycode.BACK.code = context.resources.getInteger(R.integer.key_code_keyboard_back)

        keyPopupWidth = context.resources.getInteger(R.integer.size_key_popup_width) // todo: investigate
        keyPopupHeight = context.resources.getInteger(R.integer.size_key_popup_height)
    }

    private fun setPreview(parent: View, key: Keyboard.Key, keyPreviewWidth: Int, keyPreviewHeight: Int, delayMs: Long) {
        val popup = createAndShowPrevPopUp(parent, key.label.toString(),
                key.x, key.y,10, key.height,
                keyPreviewWidth, keyPreviewHeight) // todo create builder
        parent.postDelayed({ // todo replace with
            popup.dismiss()
        }, delayMs)
    }

    private fun createAndShowPrevPopUp(parent: View,
                                       text: String,
                                       x: Int, y: Int,
                                       correctionWidth: Int,
                                       correctionHeight: Int,
                                       width: Int, height: Int,
                                       touchableOutside: Boolean = true): PopupWindow {
        val popup = PopupWindow(this)
        popup.contentView = layoutInflater.inflate(R.layout.popup_key_preview, null)
        popup.isOutsideTouchable = touchableOutside
        popup.contentView.findViewById<TextView>(R.id.tvPreview).text = text
        popup.showAtLocation(parent, Gravity.NO_GRAVITY, x - correctionWidth, y - correctionHeight)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            val density = resources.displayMetrics.density
//            popup.update((width * density).toInt(), (height * density).toInt())
            popup.update(width, height)
        }
        return popup
    }

    override fun onCreateCandidatesView(): View {
        val view = layoutInflater.inflate(R.layout.popup_key_preview, null)
        Log.d(LOG_TAG, "onCreateCandidatesView()")
        return view
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
        KB(0),
        BACK(0)
    }
}



