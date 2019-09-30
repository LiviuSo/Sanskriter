package com.android.lvicto.sanskritkeyboard.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.media.Image
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.data.Suggestion
import com.android.lvicto.sanskritkeyboard.service.KeyboardSwitch
import com.android.lvicto.sanskritkeyboard.service.SanskritCustomKeyboard.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.ui.SettingsActivity
import com.android.lvicto.sanskritkeyboard.utils.*
import com.android.lvicto.sanskritkeyboard.utils.Constants.MAX_INPUT_LEN
import com.android.lvicto.sanskritkeyboard.viewmodel.SuggestionViewModel
import java.util.concurrent.atomic.AtomicBoolean

// todo convert layouts fully to ConstraintLayout
// todo create custom view for keys
// todo show digits when closing/opening the keyboard
// todo capital letter of the beginning of the sentence (after . ! ? and a spaces)
// todo create a component that manages the runnable (refactoring)
// todo create a component that manages the suggestions (refactoring)
// todo add toggle background
abstract class KbLayoutInitializer(val context: Context) {

    protected abstract fun initExtraCodes()
    protected abstract fun getView(): View

    private val isCapsFirstLetter: Boolean = true // todo get from settings
    private lateinit var toggleDigitsKey: ImageButton
    private var isSticky: Boolean = false
    private val autoAddSpace: Boolean = true // todo make a setting
    protected val extraKeys: ArrayList<Button> = arrayListOf()
    protected var extraKeysCodesMap = hashMapOf<Int, List<Int>>()

    lateinit var ic: InputConnection // todo set it through the interface (keyboardSwitch)
    lateinit var currentInputEditorInfo: EditorInfo
    private lateinit var actionButton: ImageButton
    lateinit var keyboardSwitch: KeyboardSwitch
    private lateinit var mSugg1: Button
    private lateinit var mSugg2: Button
    private lateinit var mSugg3: Button
    private val suggViewModel = SuggestionViewModel()
    private val mTypedText = StringBuffer()
    var justAddSuggestions: Boolean = true
    protected val longPressedKeysViews = arrayListOf<View>()
    var typedText = StringBuffer()


    protected open fun toggleShiftBack() {
        // to override
    }

    protected abstract fun getInstance(): KbLayoutInitializer

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
                if (System.currentTimeMillis() - actionTime > LONG_PRESS_TIME) {
                    keyView.post { updateKeyboard(keyView) }
                    longTap.set(true)
                    actionDownFlag.set(true) // once long tap reached, end the thread
                }
            }
        }

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.background = ContextCompat.getDrawable(context, R.drawable.key_pressed)
                    actionTime = System.currentTimeMillis()
                    actionDownFlag = AtomicBoolean(false)
                    longTap = AtomicBoolean(false)
                    keyView = view
                    Thread(runnable).start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    actionDownFlag.set(true)
                    if (!longTap.get()) { // no tap; space key key_normal functionality
                        val output = " "
                        ic.commitText(output, 1)
                        // check if toggle caps is required
                        val isQwerty = getInstance() is KbLayoutInitPhoneQwertyPortrait
                        if(isCapsFirstLetter && isQwerty && isStopExclamationQuestion()) {
                            (getInstance() as KbLayoutInitPhoneQwertyPortrait).forceAllCaps(true)
                        }
                        showDigits()
                    }
                    justAddSuggestions = false
                    view.background = ContextCompat.getDrawable(context, R.drawable.key_normal)
                    view.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private val settingsOnTouchListener = View.OnTouchListener { _, motionEvent ->
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
            flagKeyDown = AtomicBoolean(false)
            actionTime = System.currentTimeMillis()
            while (!flagKeyDown.get()) {
                if (System.currentTimeMillis() - actionTime > DELAY_AUTOREPEAT) {
                    ic.deleteSurroundingText(1, 0)
                    toggleAllCapsFirstLetter()
                    // update suggestions
                    if (mTypedText.isNotEmpty()) {
                        mTypedText.delete(mTypedText.length - 1, mTypedText.length)
                    }
                    actionTime = System.currentTimeMillis()
                }
            }
        }

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    keyView = view
                    view.background = ContextCompat.getDrawable(context, R.drawable.key_pressed)
                    showDigits()

                    // delete the first char
                    ic.deleteSurroundingText(1, 0)
                    toggleAllCapsFirstLetter()

                    // start the thread
                    Thread(runnable).start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    flagKeyDown.set(true)
                    view.performClick()
                    justAddSuggestions = false
                    view.background = ContextCompat.getDrawable(context, R.drawable.key_normal)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private val actionOnTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                view.background = ContextCompat.getDrawable(context, R.drawable.key_pressed)
                showDigits()
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
                justAddSuggestions = true
                view.background = ContextCompat.getDrawable(context, R.drawable.key_normal)
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
                Log.d(LOG_TAG, "justAddSuggestions: $justAddSuggestions")
                val text = "${(view as Button).text}"
                if (text.isNotEmpty()) {
                    if (!justAddSuggestions) { // replace current word with suggestion
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
                    if (autoAddSpace && isLastWord()) {
                        ic.commitText(" ", 1)
                    }
                }
                mTypedText.delete(0, mTypedText.length)
                showSuggestions()
                true
            }
            MotionEvent.ACTION_UP -> {
                justAddSuggestions = true // suggestion was just tapped
                true
            }
            else -> {
                false
            }
        }
    }

    private val toogleDigitsTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                resetLongPressedKeys()
                showDigits()
                true
            }
            MotionEvent.ACTION_UP -> {
                view.performClick()
                true
            }
            else -> {
                false
            }
        }
    }

    protected fun getSymbolKeyTouchListener(code: Int) = object : View.OnTouchListener {

        private var actionTime: Long = 0
        private lateinit var flagActionUp: AtomicBoolean
        lateinit var keyView: View

        val runnable = Runnable {
            // reset any potentially long pressed key
            resetLongPressedKeys()
            keyView.background = ContextCompat.getDrawable(context, R.drawable.key_pressed)
            flagActionUp = AtomicBoolean(false)
            actionTime = System.currentTimeMillis()
            while (!flagActionUp.get()) {
                if (System.currentTimeMillis() - actionTime > LONG_PRESS_TIME) { // got a long tap
                    flagActionUp.set(true)
                    isSticky = true
                    longPressedKeysViews.add(keyView)
                }
            }
        }

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    keyView = view
                    showSuggestions(View.GONE, View.GONE, View.GONE)
                    mTypedText.delete(0, mTypedText.length)

                    Thread(runnable).start()
                    showExtraKeys(code)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    flagActionUp.set(true)
                    if (!isSticky) {
                        view.background = ContextCompat.getDrawable(context, R.drawable.key_normal)
                    }
                    view.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    protected fun resetLongPressedKeys() {
        if (longPressedKeysViews.isNotEmpty()) {
            longPressedKeysViews[0].background = ContextCompat.getDrawable(context, R.drawable.key_normal)
            longPressedKeysViews.clear()
            isSticky = false
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
        actionButton.setImageResource(getIconRes())
    }

    protected fun getCommonTouchListener(text: String = "", isExtra: Boolean = false) = object : View.OnTouchListener {
        private var popup: PopupWindow? = null
        private var actionTime: Long = 0
        private lateinit var flagActionUp: AtomicBoolean
        private lateinit var longTap: AtomicBoolean
        private lateinit var keyView: View
        private lateinit var output: String

        val runnable = Runnable {
            longTap = AtomicBoolean(false)
            flagActionUp = AtomicBoolean(true)
            if (!isExtra) { // reset the background
                keyView.background = ContextCompat.getDrawable(context, R.drawable.key_pressed)
            } else {
                keyView.background = ContextCompat.getDrawable(context, R.drawable.key_extra_pressed)
            }
            actionTime = System.currentTimeMillis()
            while (flagActionUp.get()) {
                if (System.currentTimeMillis() - actionTime > LONG_PRESS_TIME) { // got a long tap
                    flagActionUp.set(false)
                    longTap.set(true)
                    resetAndShowExtras(true)
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
                    // show preview (if not isExtra)
                    if (!isExtra) {
                        resetLongPressedKeys()
                        val rect = view.locateView()
                        popup = view.createPopup(output)
                        popup?.show(view, rect)
                    }
                    keyView = view
                    Thread(runnable).start() // wait for long tap
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
                        if (!isExtra) {
                            resetAndShowExtras()
                        } else if (!isSticky) {
                            showDigits()
                        }
                    }
                    if (!isExtra) { // reset the background
                        keyView.background = ContextCompat.getDrawable(context, R.drawable.key_normal)
                    } else {
                        keyView.background = ContextCompat.getDrawable(context, R.drawable.key_extra_normal)
                    }
                    justAddSuggestions = false
                    view.performClick()
                    true
                }
                else -> false
            }
        }

        private fun resetAndShowExtras(sticky: Boolean = false) {
            if (!sticky) {
                // show extra keys
                keyView.post { showDigits() }
            }
            if (!isExtra) {
                keyView.post { showExtraKeys(output[0].toInt()) }
            }
            // toggle shift back (if not in permanent state)
            toggleShiftBack()
            // close preview pop-up
            keyView.postDelayed({
                popup?.dismiss()
            }, DELAY_HIDE_PREVIEW)
            isSticky = sticky
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
        }
        if (mSugg2.visibility != vis2) {
            mSugg2.visibility = vis2
        }
        if (mSugg3.visibility != vis3) {
            mSugg3.visibility = vis3
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
        view.findViewById<ImageButton>(R.id.keyDel).apply {
            setOnTouchListener(deleteOnTouchListener)
        }
        (view imageButton R.id.keyAction).apply {
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
        showSuggestions()
    }

    private fun getIconRes(): Int =
            when (currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
                EditorInfo.IME_ACTION_NONE -> {
                    R.drawable.ic_done_white_24dp
                }
                EditorInfo.IME_ACTION_GO -> {
                    R.drawable.ic_send_white_24dp
                }
                EditorInfo.IME_ACTION_SEARCH -> {
                    R.drawable.ic_search_white_24dp
                }
                EditorInfo.IME_ACTION_SEND -> {
                    R.drawable.ic_send_white_24dp
                }
                EditorInfo.IME_ACTION_NEXT -> {
                    R.drawable.ic_navigate_next_white_24dp
                }
                EditorInfo.IME_ACTION_DONE -> {
                    R.drawable.ic_search_white_24dp
                }
                EditorInfo.IME_ACTION_PREVIOUS -> {
                    R.drawable.ic_navigate_before_white_24dp
                }
                else -> {
                    R.drawable.ic_done_white_24dp
                }
            }

    protected fun showExtraKeys(code: Int) {
        val relatedChars = getRelatedCharsRes(code)
        if (relatedChars != null && relatedChars.size > 0) {
            extraKeys.forEach {
                it.text = context.resources.getString(R.string.key_label_placeholder)
                it.isEnabled = false
            }
            Log.d(LOG_TAG, "relatedChars.size: ${relatedChars.size}")
            (0 until relatedChars.size).forEach { index ->
                extraKeys[index].isEnabled = true
                extraKeys[index].text = "${relatedChars[index].toChar()}"
            }
            // show "toggle to digits" if there are extras
            toggleDigitsKey.visibility = View.VISIBLE
        } else { // if no extras just show the digits // todo optimize logic
            showDigits()
        }
    }

    protected fun initExtraKeys(view: View) {
        toggleDigitsKey = (view imageButton R.id.keyToggleDigits).apply {
            setOnTouchListener(toogleDigitsTouchListener)
        }
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
                , view button R.id.keyLetterExtra10))
        extraKeys.forEach {
            it.setOnTouchListener(getCommonTouchListener(isExtra = true))
        }
        showDigits()
    }

    fun showDigits() {
        if (toggleDigitsKey.visibility != View.GONE) {
            toggleDigitsKey.visibility = View.GONE
            (0..9).forEach {
                extraKeys[it].isEnabled = true
                extraKeys[it].text = extraKeysCodesMap[R.integer.key_code_digits.getVal(context)]?.get(it)?.toChar().toString()
            }
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

    fun isTypedTextEmpty(): Boolean {
        val textBefore = ic.getTextBeforeCursor(MAX_INPUT_LEN, 0)
        val textAfter = ic.getTextAfterCursor(MAX_INPUT_LEN, 0)
        return textBefore.isEmpty() && textAfter.isEmpty()
    }

    fun isSpaceAfterStopExclamationQuestion(): Boolean {
        val textBefore = ic.getTextBeforeCursor(MAX_INPUT_LEN, 0)
        val len = textBefore.length
        return textBefore[len - 1] == ' '
                && (textBefore[len - 2] == '?' || textBefore[len - 2] == '!' || textBefore[len - 2] == '.')
    }

    fun isStopExclamationQuestion(): Boolean {
        val textBefore = ic.getTextBeforeCursor(MAX_INPUT_LEN, 0)
        val len = textBefore.length
        return (textBefore[len - 2] == '?' || textBefore[len - 2] == '!' || textBefore[len - 2] == '.')
    }

    fun toggleAllCapsFirstLetter() {
        if(isCapsFirstLetter && (getInstance() is KbLayoutInitPhoneQwertyPortrait)) {
            val kbQwertyInit = getInstance() as KbLayoutInitPhoneQwertyPortrait
            if(kbQwertyInit.isTypedTextEmpty()) {
                kbQwertyInit.forceAllCaps(true)
            } else {
                if(kbQwertyInit.isSpaceAfterStopExclamationQuestion()) {
                    kbQwertyInit.forceAllCaps(true)
                } else {
                    kbQwertyInit.forceAllCaps(false)
                }
            }
        }

    }

    companion object {

        fun getLayoutInitializer(context: Context, config: KeyboardConfig): KbLayoutInitializer {

            val orientation = config.orientation
            val tablet = config.isTablet
            val keyboardType = config.type

            return when {
                (tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.QWERTY) -> {
                    KbLayoutInitTabletQwertyPortrait(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.QWERTY) -> {
                    KbLayoutInitPhoneQwertyPortrait(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.QWERTY) -> {
                    KbLayoutInitTabletQwertyLandscape(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.QWERTY) -> {
                    KbLayoutInitPhoneQwertyLandscape(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.SA) -> {
                    KbLayoutInitTabletSanskritPortrait(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.SA) -> {
                    KbLayoutInitPhoneSanskritPortrait(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.SA) -> {
                    KbLayoutInitTabletSanskritLandscape(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.SA) -> {
                    KbLayoutInitPhoneSanskritLandscape(context)
                }
                else -> {
                    throw Exception("KbLayoutInitializer: Not a valid config")
                }
            }
        }

        private const val DELAY_HIDE_PREVIEW: Long = 160
        private const val DELAY_AUTOREPEAT: Long = 160
        val LONG_PRESS_TIME = ViewConfiguration.getLongPressTimeout()

    }
}