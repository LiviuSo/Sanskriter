package com.android.lvicto.sanskritkeyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.MainThread
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.data.Suggestion
import com.android.lvicto.sanskritkeyboard.service.*
import com.android.lvicto.sanskritkeyboard.service.SanskritCustomKeyboard.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.ui.SettingsActivity
import com.android.lvicto.sanskritkeyboard.utils.Constants.MAX_INPUT_LEN
import com.android.lvicto.sanskritkeyboard.utils.button
import com.android.lvicto.sanskritkeyboard.utils.createPopup
import com.android.lvicto.sanskritkeyboard.utils.locateView
import com.android.lvicto.sanskritkeyboard.utils.show
import com.android.lvicto.sanskritkeyboard.viewmodel.SuggestionViewModel
import java.util.concurrent.atomic.AtomicBoolean

abstract class KbLayoutInitializer(val context: Context) {

    protected abstract fun initExtraCodes()
    protected abstract fun getView(): View

    protected val extraKeys: ArrayList<Button> = arrayListOf()
    protected var extraKeysCodesMap = hashMapOf<Int, List<Int>>()

    lateinit var ic: InputConnection // todo set it through the interface (keyboardSwitch)
    lateinit var currentInputEditorInfo: EditorInfo
    private lateinit var actionButton: Button
    lateinit var keyboardSwitch: KeyboardSwitch
    private lateinit var mSugg1: Button
    private lateinit var mSugg2: Button
    private lateinit var mSugg3: Button
    private val suggViewModel = SuggestionViewModel()
    private val mTypedText = StringBuffer()

    protected open fun toggleShiftBack() {
        // to override
    }

    @MainThread
    fun updateKeyboard(view: View) { // todo add animations to transitions
        keyboardSwitch.switchKeyboard()
        // change label
        val key = (view as TextView)
        if (key.text == context.getText(R.string.lang_label_qwerty)) {
            key.text = context.getText(R.string.lang_label_sa)
        } else {
            key.text = context.getText(R.string.lang_label_qwerty)
        }
    }

    private fun getSpaceKeyTouchListener() = object : View.OnTouchListener {
        var actionTime: Long = 0
        lateinit var actionDownFlag: AtomicBoolean
        lateinit var longTap: AtomicBoolean
        lateinit var keyView: View

        val runnable = Runnable {
             while (actionDownFlag.get()) {
                 Log.d(LOG_TAG, "Touching Down")
                 if(System.currentTimeMillis() - actionTime > LONG_PRESS_TIME) {
                     Log.d(LOG_TAG, "Long tap")
                     keyView.post { updateKeyboard(keyView) }
                     longTap.set(true)
                     actionDownFlag.set(false) // once long tap reached, end the thread
                 }
             }
            Log.d(LOG_TAG, "Not Touching")
        }

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    actionTime = System.currentTimeMillis()
                    actionDownFlag = AtomicBoolean(true)
                    longTap = AtomicBoolean(false)
                    keyView = view
                    Thread(runnable).start() // todo investigate : stop the thread needed ?
                    true
                }
                MotionEvent.ACTION_UP -> {
                    actionDownFlag.set(false)
                    if(!longTap.get()) { // no tap; space key normal functionality
                        val output = " "
                        ic.commitText(output, 1)
                        disableAllExtraKeys()
                        view.performClick()
                    }
                    true
                }
                else -> {
                    false
                }
            }
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
        // update suggestions
        if (mTypedText.isNotEmpty()) { // todo consider cursor position
            mTypedText.delete(mTypedText.length - 1, mTypedText.length)
            Log.d(LOG_TAG, "mTypedText : $mTypedText")
        }
        Log.d(LOG_TAG, "deleteOnClickListener : $mTypedText")
        updateSuggestions(mTypedText.toString())
    }

    // todo convert to a touch listener
    // todo investigate: what happens with the suggestions
    private val actionOnClickListener: View.OnClickListener = View.OnClickListener {
        val ei = currentInputEditorInfo
        if (ei.actionId != 0) {
            ic.performEditorAction(ei.actionId)
        } else if (ei.imeOptions and EditorInfo.IME_MASK_ACTION != EditorInfo.IME_ACTION_NONE) {
            ic.performEditorAction(ei.imeOptions and EditorInfo.IME_MASK_ACTION)
        }
    }

    // todo convert to a touch listener
    // todo show system bar when hiding the suggs
    private val suggestionOnClickListener = View.OnClickListener {
        val text = "${(it as TextView).text} " // add a space // todo make it a setting
        if (text.isNotEmpty()) {
            // replace with last part of the output with 'text'
            val index = getCursorPosition() // todo create a component that manages the suggestions
            val lengthBefore = index - ic.getTextBeforeCursor(MAX_INPUT_LEN, 0).lastIndexOf(' ') - 1  // -1 to include the space
            val lengthAfter = ic.getTextAfterCursor(MAX_INPUT_LEN, 0).indexOf(' ') + 1 // +1 to include the space
            Log.d(LOG_TAG, "index: $index indexFistSpace: ${ic.getTextAfterCursor(MAX_INPUT_LEN, 0).indexOf(' ')} lengthBefore: $lengthBefore lengthAfter: $lengthAfter ")
            ic.deleteSurroundingText(lengthBefore, lengthAfter) // delete old word
            // update cursor position and commit new word
            ic.commitText(text, 1)
        }
        mTypedText.delete(0, mTypedText.length)
        mSugg1.visibility = View.GONE
        mSugg2.visibility = View.GONE
        mSugg3.visibility = View.GONE
    }

    private fun getCursorPosition(): Int {
        val extracted: ExtractedText = ic.getExtractedText(ExtractedTextRequest(), 0)
        return extracted.startOffset + extracted.selectionStart
    }

    fun getSurroundingWord(): String = StringBuffer()
            .append(ic.getTextBeforeCursor(MAX_INPUT_LEN, 0).takeLastWhile {
                it != ' '
            }).append(ic.getTextAfterCursor(MAX_INPUT_LEN, 0).takeWhile {
                it != ' '
            })
            .toString()

    fun initKeyboard(): View = getView().apply {
        initExtraCodes()
        mTypedText.delete(0, mTypedText.length)
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
                MotionEvent.ACTION_DOWN -> { // todo show extra keys and vibrate on KEY_DOWN
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
                        ic.commitText(output, 1)

                        // update suggestions
                        if (output.isNotBlank()) {
                            mTypedText.append(output)
                            updateSuggestions(mTypedText.toString())
                        }
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

    @SuppressLint("CheckResult")
    fun updateSuggestions(string: String) {
        if (string.isNotBlank() || string.isNotEmpty()) { // no suggestions for spaces
            suggViewModel.getSuggestions(string).subscribe({
                getSuggestions(it!!)
            }, {
                Log.d(LOG_TAG, it.message ?: "No exception")
            })
        } else { // hide the suggestions
            // todo make a function
            if (mSugg1.visibility != View.GONE) {
                mSugg1.visibility = View.GONE
            }
            if (mSugg2.visibility != View.GONE) {
                mSugg2.visibility = View.GONE
            }
            if (mSugg3.visibility != View.GONE) {
                mSugg3.visibility = View.GONE
            }
        }
    }

    private fun getSuggestions(it: List<Suggestion>) {
        when (it.size) {
            0 -> {
                Log.d(LOG_TAG, "found 0 suggs; $mTypedText")
                if (mSugg1.visibility != View.VISIBLE) {
                    mSugg1.visibility = View.VISIBLE
                }
                if (mSugg2.visibility != View.GONE) {
                    mSugg2.visibility = View.GONE
                }
                if (mSugg3.visibility != View.GONE) {
                    mSugg3.visibility = View.GONE
                }
                mSugg1.text = mTypedText
                mSugg1.tag = mTypedText
            }
            1 -> {
                Log.d(LOG_TAG, "found 1 suggs; $mTypedText")
                if (mSugg1.visibility != View.VISIBLE) {
                    mSugg1.visibility = View.VISIBLE
                }
                if (mSugg2.visibility != View.GONE) {
                    mSugg2.visibility = View.GONE
                }
                if (mSugg3.visibility != View.GONE) {
                    mSugg3.visibility = View.GONE
                }
                mSugg1.text = it[0].word
            }
            2 -> {
                Log.d(LOG_TAG, "found 2 suggs; $mTypedText")
                if (mSugg1.visibility != View.VISIBLE) {
                    mSugg1.visibility = View.VISIBLE
                }
                if (mSugg2.visibility != View.VISIBLE) {
                    mSugg2.visibility = View.VISIBLE
                }
                if (mSugg3.visibility != View.GONE) {
                    mSugg3.visibility = View.GONE
                }
                mSugg1.text = it[0].word
                mSugg2.text = it[1].word
            }
            else -> { // 3 or more
                Log.d(LOG_TAG, "found 3+ suggs; $mTypedText")
                if (mSugg1.visibility != View.VISIBLE) {
                    mSugg1.visibility = View.VISIBLE
                }
                if (mSugg2.visibility != View.VISIBLE) {
                    mSugg2.visibility = View.VISIBLE
                }
                if (mSugg3.visibility != View.VISIBLE) {
                    mSugg3.visibility = View.VISIBLE
                }
                mSugg1.text = it[0].word
                mSugg2.text = it[1].word
                mSugg3.text = it[2].word
            }
        }
    }

    /**
     * Set common keys
     */
    protected open fun bindKeys(view: View) {
        (view button R.id.keySpace).apply {
            setOnTouchListener(getSpaceKeyTouchListener())
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
        // bind suggestions
        mSugg1 = (view button R.id.keySuggestion1).apply {
            setOnClickListener(suggestionOnClickListener)
            visibility = View.GONE
        }
        mSugg2 = (view button R.id.keySuggestion2).apply {
            setOnClickListener(suggestionOnClickListener)
            visibility = View.GONE
        }
        mSugg3 = (view button R.id.keySuggestion3).apply {
            setOnClickListener(suggestionOnClickListener)
            visibility = View.GONE
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