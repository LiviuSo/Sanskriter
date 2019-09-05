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
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.MainThread
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.data.Suggestion
import com.android.lvicto.sanskritkeyboard.service.*
import com.android.lvicto.sanskritkeyboard.service.SanskritCustomKeyboard.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.ui.SettingsActivity
import com.android.lvicto.sanskritkeyboard.utils.*
import com.android.lvicto.sanskritkeyboard.utils.Constants.MAX_INPUT_LEN
import com.android.lvicto.sanskritkeyboard.viewmodel.SuggestionViewModel
import java.util.concurrent.atomic.AtomicBoolean

abstract class KbLayoutInitializer(val context: Context) {

    protected abstract fun initExtraCodes()
    protected abstract fun getView(): View

    private lateinit var mSep1: FrameLayout
    private lateinit var mSep2: FrameLayout
    private lateinit var mSep3: FrameLayout
    private val autoAddSpace: Boolean = true // todo make a setting
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
    var justAddSugg: Boolean = true

    protected open fun toggleShiftBack() {
        // to override
    }

    @MainThread
    fun updateKeyboard(view: View) { // todo add animations to switching
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
            while (!actionDownFlag.get()) {
                Log.d(LOG_TAG, "Touching Down")
                if (System.currentTimeMillis() - actionTime > LONG_PRESS_TIME) {
                    Log.d(LOG_TAG, "Long tap")
                    keyView.post { updateKeyboard(keyView) }
                    longTap.set(true)
                    actionDownFlag.set(true) // once long tap reached, end the thread
                }
            }
            Log.d(LOG_TAG, "Not Touching")
        }

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    actionTime = System.currentTimeMillis()
                    actionDownFlag = AtomicBoolean(false)
                    longTap = AtomicBoolean(false)
                    keyView = view
                    Thread(runnable).start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    actionDownFlag.set(true)
                    if (!longTap.get()) { // no tap; space key normal functionality
                        val output = " "
                        ic.commitText(output, 1)
                        disableAllExtraKeys()
                        view.performClick()
                    }
                    justAddSugg = false
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private val settingsOnTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                context.startActivity(SettingsActivity.intent(context))
                true
            }
            MotionEvent.ACTION_UP -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private val deleteOnTouchListener = object : View.OnTouchListener {
        lateinit var flagKeyDown: AtomicBoolean
        var actionTime = 0L
        var keyView: View? = null

        val runnable = Runnable {
            while (!flagKeyDown.get()) {
                if (System.currentTimeMillis() - actionTime > DELAY_AUTOREPEAT) {
                    Log.d(LOG_TAG, "delete char")
                    actionTime = System.currentTimeMillis()
                    ic.deleteSurroundingText(1, 0)
                    // update suggestions
                    if (mTypedText.isNotEmpty()) {
                        mTypedText.delete(mTypedText.length - 1, mTypedText.length)
                        Log.d(LOG_TAG, "mTypedText : $mTypedText")
                    }
                }
            }
        }

        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            return when (motionEvent?.action) {
                MotionEvent.ACTION_DOWN -> {
                    disableAllExtraKeys()

                    // delete the first char
                    ic.deleteSurroundingText(1, 0)
                    // start the thread
                    keyView = view
                    flagKeyDown = AtomicBoolean(false)
                    actionTime = System.currentTimeMillis()
                    Thread(runnable).start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    flagKeyDown.set(true)
                    view?.performClick()
                    justAddSugg = false
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private val actionOnTouchListener = View.OnTouchListener { _, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                val ei = currentInputEditorInfo
                if (ei.actionId != 0) {
                    ic.performEditorAction(ei.actionId)
                } else if (ei.imeOptions and EditorInfo.IME_MASK_ACTION != EditorInfo.IME_ACTION_NONE) {
                    ic.performEditorAction(ei.imeOptions and EditorInfo.IME_MASK_ACTION)
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                updateSuggestions("")
                justAddSugg = true
                true
            }
            else -> {
                false
            }
        }
    }

    private val suggestionOnTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(LOG_TAG, "justAddSugg: $justAddSugg")
                val text = "${(view as Button).text}"
                if (text.isNotEmpty()) {  // todo create a component that manages the suggestions (refactoring)
                    if (!justAddSugg) { // replace current word with suggestion
                        val lengthBefore = getBeforeCursorInSurroundingWord().length
                        val lengthAfter = getAfterCursorInSurroundingWord().length
                        Log.d(LOG_TAG, "suggestionOnTouchListener: $lengthBefore $lengthAfter")

                        // replace surrounding word with suggestion
                        ic.deleteSurroundingText(lengthBefore, lengthAfter) // delete old word

                    } else { // subsequent tap on suggestion -> prepend a space
                        if (autoAddSpace && !isLastWord()) {
                            ic.commitText(" ", 1)
                        }
                    }
                    // commit new word
                    ic.commitText(text, 1)

                    // append space
                    if(autoAddSpace && isLastWord()) {
                        ic.commitText(" ", 1)
                    }
                }
                mTypedText.delete(0, mTypedText.length)
                showSuggestions()
                true
            }
            MotionEvent.ACTION_UP -> {
                justAddSugg = true // suggestion was just tapped
                true
            }
            else -> {
                false
            }
        }
    }

    private fun isLastWord(): Boolean {
        val se = ic.getTextAfterCursor(MAX_INPUT_LEN, 0) ?: return true
        return se.isEmpty()
    }

    @Synchronized
    fun getBeforeCursorInSurroundingWord(): String {
        val before = ic.getTextBeforeCursor(MAX_INPUT_LEN, 0)
        if (before.isNullOrEmpty()) {
            return ""
        }
        return StringBuffer().append(before.takeLastWhile {
            it != ' '
        }).toString()
    }

    @Synchronized
    fun getAfterCursorInSurroundingWord(): String {
        val after = ic.getTextAfterCursor(MAX_INPUT_LEN, 0)
        if (after.isNullOrEmpty()) {
            return ""
        }
        return StringBuffer()
                .append(after.takeWhile {
                    it != ' '
                }).toString()
    }

    fun initKeyboardView(): View = getView().apply {
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
        private var actionTime: Long = 0
        private lateinit var flagActionUp: AtomicBoolean
        private lateinit var longTap: AtomicBoolean
        private lateinit var keyView: View
        private lateinit var output: String

        val runnable = Runnable {
            // todo make it more general - to be used in spaceKeyTouchListener as well
            // show candidates
            while (flagActionUp.get()) {
                if (System.currentTimeMillis() - actionTime > LONG_PRESS_TIME) { // got a long tap
                    flagActionUp.set(false)
                    longTap.set(true)
                    resetAndShowExtras()
                }
            }
        }

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            output = if (text.isEmpty()) {
                (view as Button).text.toString()
            } else {
                text
            }

            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> { // todo key showing the preview till ACTION_DOWN (if no extras)
                    Log.d(LOG_TAG, "Action was DOWN")

                    flagActionUp = AtomicBoolean(true)
                    longTap = AtomicBoolean(false)
                    keyView = view

                    actionTime = System.currentTimeMillis()
                    Log.d(LOG_TAG, "DOWN: $actionTime")

                    Thread(runnable).start() // wait for long tap

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
                    flagActionUp.set(false)

                    if (!longTap.get()) {
                        Log.d(LOG_TAG, "Normal tap")
                        // send text if not long tap
                        ic.commitText(output, 1)
                        // show extras (and close the preview)
                        resetAndShowExtras()
                    }
                    view.performClick()
                    justAddSugg = false
                    true
                }
                else -> false
            }
        }

        private fun resetAndShowExtras() {
            // show extra keys
            keyView.post { disableAllExtraKeys() }
            if (!extra) {
                keyView.post { showExtraKeys(output[0].toInt()) }
                // close preview pop-up
                keyView.postDelayed({
                    popup?.dismiss()
                }, DELAY_HIDE_PREVIEW)
            }
            // toggle shift back (if not in permanent state)
            toggleShiftBack()
        }
    }

    @SuppressLint("CheckResult")
    fun updateSuggestions(string: String) {
        resetTypedString(string)
        if (string.isNotBlank() || string.isNotEmpty()) { // no suggestions for spaces
            suggViewModel.getSuggestions(string).subscribe({
                getSuggestions(it!!)
            }, {
                Log.d(LOG_TAG, it.message ?: "No exception")
            })
        } else { // hide the suggestions
            showSuggestions()
        }
    }

    private fun showSuggestions(vis1: Int = View.GONE, vis2: Int = View.GONE, vis3: Int = View.GONE) {
        if (mSugg1.visibility != vis1) {
            mSugg1.visibility = vis1
            mSep1.visibility = vis1
        }
        if (mSugg2.visibility != vis2) {
            mSugg2.visibility = vis2
            mSep2.visibility = vis2
        }
        if (mSugg3.visibility != vis3) {
            mSugg3.visibility = vis3
            mSep3.visibility = vis3
        }
    }

    private fun getCursorPosition(): Int {
        val extracted: ExtractedText = ic.getExtractedText(ExtractedTextRequest(), 0)
        return extracted.startOffset + extracted.selectionStart
    }

    private fun getSuggestions(it: List<Suggestion>) {
        when (it.size) {
            0 -> {
                Log.d(LOG_TAG, "found 0 suggs; $mTypedText")
                if (mSugg1.visibility != View.VISIBLE && mTypedText.isNotEmpty()) {
                    mSugg1.visibility = View.VISIBLE
                    mSep1.visibility = View.VISIBLE
                }
                showSuggestions(mSugg1.visibility)
                mSugg1.text = mTypedText
                mSugg1.tag = mTypedText
            }
            1 -> {
                Log.d(LOG_TAG, "found 1 suggs; $mTypedText")
                showSuggestions(View.VISIBLE, View.GONE, View.GONE)
                mSugg1.text = it[0].word
            }
            2 -> {
                Log.d(LOG_TAG, "found 2 suggs; $mTypedText")
                showSuggestions(View.VISIBLE, View.VISIBLE, View.GONE)
                mSugg1.text = it[0].word
                mSugg2.text = it[1].word
            }
            else -> { // 3 or more
                Log.d(LOG_TAG, "found 3+ suggs; $mTypedText")
                showSuggestions(View.VISIBLE, View.VISIBLE, View.VISIBLE)
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
            setOnTouchListener(deleteOnTouchListener)
        }
        (view button R.id.keyAction).apply {
            actionButton = this
            setOnTouchListener(actionOnTouchListener)
        }
        (view button R.id.keySettings).apply {
            setOnTouchListener(settingsOnTouchListener)
        }
        // bind suggestions
        mSugg1 = (view button R.id.keySuggestion1).apply {
            setOnTouchListener(suggestionOnTouchListener)
        }
        mSugg2 = (view button R.id.keySuggestion2).apply {
            setOnTouchListener(suggestionOnTouchListener)
        }
        mSugg3 = (view button R.id.keySuggestion3).apply {
            setOnTouchListener(suggestionOnTouchListener)
        }
        mSep1 = view frameLayout R.id.sep1
        mSep2 = view frameLayout R.id.sep2
        mSep3 = view frameLayout R.id.sep3
        showSuggestions()
    }

    private fun getActionTypeString() = when (currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
        EditorInfo.IME_ACTION_NONE -> {
            "AC" // todo add icons
        }
        EditorInfo.IME_ACTION_GO -> {
            "GO"
        }
        EditorInfo.IME_ACTION_SEARCH -> {
            "SC"
        }
        EditorInfo.IME_ACTION_SEND -> {
            "SE"
        }
        EditorInfo.IME_ACTION_NEXT -> {
            "->"
        }
        EditorInfo.IME_ACTION_DONE -> {
            "OK"
        }
        EditorInfo.IME_ACTION_PREVIOUS -> {
            "<-"
        }
        else -> {
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

    fun resetTypedString(s: String = "") {
        mTypedText.replace(0, mTypedText.length, s)
    }

    fun isTheMiddleOfWord(): Boolean {
        val before = getBeforeCursorInSurroundingWord()
        val after = getAfterCursorInSurroundingWord()
        Log.d(LOG_TAG, "isTheMiddleOfWord(): [$before][$after]")

        return before.isNotEmpty() && after.isNotEmpty()
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

        private const val DELAY_HIDE_PREVIEW: Long = 160
        private const val DELAY_AUTOREPEAT: Long = 100
        val LONG_PRESS_TIME = ViewConfiguration.getLongPressTimeout()

    }
}