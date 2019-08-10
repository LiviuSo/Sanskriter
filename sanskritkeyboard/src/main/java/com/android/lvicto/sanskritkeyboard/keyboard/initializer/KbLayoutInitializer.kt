package com.android.lvicto.sanskritkeyboard.keyboard.initializer

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.android.lvicto.sanskritkeyboard.*
import com.android.lvicto.sanskritkeyboard.CustomKeyboard2.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType
import java.lang.Exception

abstract class KbLayoutInitializer(val context: Context) {

    protected abstract fun initExtraCodes()
    protected abstract fun getView(): View

    protected val extraKeys: ArrayList<Button> = arrayListOf()
    protected var extraKeysCodesMap = hashMapOf<Int, List<Int>>()

    lateinit var ic: InputConnection
    lateinit var currentInputEditorInfo: EditorInfo
    lateinit var actionButton: Button
    lateinit var keyboardSwitch: KeyboardSwitch

    protected open fun toggleShiftBack() {
        // to override
    }

    // todo convert to a touch listener
    private val spaceClickListener: View.OnClickListener = View.OnClickListener {
        val output = " "
        ic.commitText(output, output.length)
        disableAllExtraKeys()
        val key = (it as TextView)
        if (key.text == "Qwerty") { // todo add resources & fix bug (text changing at tap)
            key.text = "Sanskrit"
        } else {
            key.text = "Qwerty"
        }
    }

    // todo convert to a touch listener
    private val settingsKeyClickListener = View.OnClickListener {
        context.startActivity(SettingsActivity.intent(context))
    }

    // todo convert to a touch listener
    private val deleteOnClickListener: View.OnClickListener = View.OnClickListener {
        Log.d(LOG_TAG, "deleteOnClickListener: $ic")
        ic.deleteSurroundingText(1, 0)
        disableAllExtraKeys()
    }

    // todo convert to a touch listener
    private val actionOnClickListener: View.OnClickListener = View.OnClickListener {
        val ei = currentInputEditorInfo
        if (ei.actionId != 0) {
            ic.performEditorAction(ei.actionId)
        } else if (ei.imeOptions and EditorInfo.IME_MASK_ACTION != EditorInfo.IME_ACTION_NONE) {
            ic.performEditorAction(ei.imeOptions and EditorInfo.IME_MASK_ACTION)
        }
    }

    fun initKeyboard(): View = getView().apply {
        initExtraCodes()
        bindKeys(this)
    }

    fun setActionType() {
        Log.d(LOG_TAG, "setActionType() $actionButton")
        actionButton.text = getActionTypeString()
    }

    protected fun getCommonTouchListener(text: String = "", extra: Boolean = false) = object : View.OnTouchListener {
        private var popup: PopupWindow? = null
        var actionTime: Long = 0

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            val output = if (text.isEmpty()) {
                (view as Button).text.toString()
            } else {
                text
            }

            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(LOG_TAG, "Action was DOWN")

                    actionTime = System.currentTimeMillis()
                    Log.d(LOG_TAG, "DOWN: $actionTime")

                    // show preview (if not extra)
                    if (!extra) {
                        val rect = view.locateView()
                        Log.d(LOG_TAG, "${rect.left} ${rect.right} ${rect.bottom} ${rect.top} ")
                        popup = view.createPopup(output)
                        popup?.show(view, rect)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(LOG_TAG, "Action was UP")

                    val delay = System.currentTimeMillis() - actionTime
                    if (delay >= LONG_PRESS_TIME) {
                        Log.d(LOG_TAG, "Long press: $delay $LONG_PRESS_TIME")
                        // do smth
                    } else {
                        // send text if not long tap
                        ic.commitText(output, output.length)
                    }
                    // show extra keys
                    disableAllExtraKeys()
                    if (!extra) {
                        showExtraKeys(output[0].toInt())
                        // close preview pop-up
                        view.postDelayed({
                            popup?.dismiss()
                        }, DELAY_HIDE_PREVIEW)
                    }
                    // toggle shift back (if not in permanent state)
                    toggleShiftBack()
                    view.performClick()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Set common keys
     */
    protected open fun bindKeys(view: View) {
        (view button R.id.keySpace).apply {
            setOnClickListener(spaceClickListener)
            setOnLongClickListener {
                keyboardSwitch.switchKeyboard()
                true
            }
        }
        view.findViewById<Button>(R.id.keyDel).apply {
            setOnClickListener(deleteOnClickListener)
        }
        (view button R.id.keyAction).apply {
            actionButton = this
            setOnClickListener(actionOnClickListener)
        }
        (view button R.id.keySettings).apply {
            setOnClickListener(settingsKeyClickListener)
        }
    }

    private fun getActionTypeString() = when (currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
        EditorInfo.IME_ACTION_NONE -> {
            Log.d(LOG_TAG, "IME_ACTION_NONE")
            "AC" // todo create resources & remove logs
        }
        EditorInfo.IME_ACTION_GO -> {
            Log.d(LOG_TAG, "IME_ACTION_GO")
            "GO"
        }
        EditorInfo.IME_ACTION_SEARCH -> {
            Log.d(LOG_TAG, "IME_ACTION_SEARCH")
            "SC"
        }
        EditorInfo.IME_ACTION_SEND -> {
            Log.d(LOG_TAG, "IME_ACTION_SEND")
            "SE"
        }
        EditorInfo.IME_ACTION_NEXT -> {
            Log.d(LOG_TAG, "IME_ACTION_NEXT")
            "->"
        }
        EditorInfo.IME_ACTION_DONE -> {
            Log.d(LOG_TAG, "IME_ACTION_DONE")
            "OK"
        }
        EditorInfo.IME_ACTION_PREVIOUS -> {
            Log.d(LOG_TAG, "IME_ACTION_PREVIOUS")
            "<-"
        }
        else -> {
            Log.d(LOG_TAG, "Default ime")
            "AC"
        }
    }

    protected fun showExtraKeys(code: Int) {
        val relatedChars = getRelatedCharsRes(code)
        if (relatedChars != null) {
            Log.d(LOG_TAG, "relatedChars.size: ${relatedChars.size}")
            (0 until relatedChars.size).forEach { index ->
                extraKeys[index].text = "${relatedChars[index].toChar()}"
                extraKeys[index].isEnabled = true
            }
        }
    }

    protected fun disableAllExtraKeys() {
        extraKeys.forEach {
            it.text = context.resources.getString(R.string.key_label_placeholder)
            it.isEnabled = false
        }
    }

    protected fun initExtraKeys(view: View) {
        extraKeys.addAll(arrayListOf(
                view button R.id.keyLetterExtra1
                , view button R.id.keyLetterExtra2
                , view button R.id.keyLetterExtra3
                , view button R.id.keyLetterExtra4
                , view button R.id.keyLetterExtra5
                , view button R.id.keyLetterExtra6
                , view button R.id.keyLetterExtra7
                , view button R.id.keyLetterExtra8
                , view button R.id.keyLetterExtra9
                , view button R.id.keyLetterExtra10
                , view button R.id.keyLetterExtra11
                , view button R.id.keyLetterExtra12
                , view button R.id.keyLetterExtra13
                , view button R.id.keyLetterExtra14
                , view button R.id.keyLetterExtra15
                , view button R.id.keyLetterExtra16
                , view button R.id.keyLetterExtra17
                , view button R.id.keyLetterExtra18)
        )
        extraKeys.forEach {
            it.isEnabled = false
            it.setOnTouchListener(getCommonTouchListener(extra = true))
        }
    }

    private fun getRelatedCharsRes(code: Int): ArrayList<Int>? =
            if (extraKeysCodesMap.containsKey(code)) {
                extraKeysCodesMap[code] as ArrayList<Int>
            } else {
                null
            }

    companion object {

        fun getLayoutInitializer(context: Context, config: KeyboardConfig): KbLayoutInitializer {

            val orientation = config.orientation
            val tablet = config.isTablet
            val keyboardType = config.type

            return when {
                (tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.QWERTY) -> {
                    KbLayoutInitializerTabletPortraitQwerty(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.QWERTY) -> {
                    KbLayoutInitializerPhonePortraitQwerty(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.QWERTY) -> {
                    KbLayoutInitializerTabletLanscapeQwerty(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.QWERTY) -> {
                    KbLayoutInitializerPhoneLandscapeQwertyPhonePortraitQwerty(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.SA) -> {
                    KbLayoutInitializerTabletPortraitSa(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.SA) -> {
                    KbLayoutInitializerPhonePortraitSa(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.SA) -> {
                    KbLayoutInitializerTabletLandscapeSa(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.SA) -> {
                    KbLayoutInitializerPhoneLandscapeSaPhonePortraitSa(context)
                }
                else -> {
                    throw Exception("KbLayoutInitializer: Not a valid config")
                }
            }
        }

        private const val DELAY_HIDE_PREVIEW: Long = 120
        private val LONG_PRESS_TIME = ViewConfiguration.getLongPressTimeout()

    }
}