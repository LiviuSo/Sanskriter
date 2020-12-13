package com.lvicto.skeyboard.service

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.skeyboard.R
import com.lvicto.skeyboard.Injector
import com.lvicto.skeyboard.KeyboardApplication.Companion.keyboardApplication
import com.lvicto.skeyboard.KeyboardType
import com.lvicto.skeyboard.activity.SettingsActivity
import com.lvicto.skeyboard.view.key.CandidatesKeyView
import com.lvicto.skeyboard.view.key.ExtraIastShiftKeyView
import com.lvicto.skeyboard.view.keyboard.CustomKeyboardView
import com.lvicto.skeyboard.view.keyboard.IastKeyboardView
import com.lvicto.skeyboard.view.keyboard.QwertyKeyboardView
import com.lvicto.skeyboard.view.keyboard.SanskritKeyboardView
import com.lvicto.skeyboard.viewmodel.SuggestionViewModel
import java.util.*

/**
 * Will change the keyboard type according to the actionLabel field (for now)
 */
class SanskritKeyboardIms : InputMethodService(), LifecycleOwner {

    var inputConnectionWrapper: InputConnectionWrapper2? = null

    private val injector = Injector.getInstance(this)

    // region keyboard view
    lateinit var keyboardView: CustomKeyboardView
    private lateinit var qwertyKeyboardView: QwertyKeyboardView
    private lateinit var sansKeyboardView: SanskritKeyboardView
    private lateinit var iastKeyboardView: IastKeyboardView
    // endregion

    // region keyboard type
    private var _keyboardType: KeyboardType = KeyboardType.QWERTY
    private var keyboardType: KeyboardType
        get() {
            return _keyboardType
        }
        set(value) {
            _keyboardType = value
            keyboardView = getKeyboardViewForType(keyboardType)
        }

    private fun updateKeyboardType(type: KeyboardType) {
        val handler = Handler(mainLooper)
        keyboardType = type
        keyboardView.let {
            handler.post { // not working
                setInputView(keyboardView)
            }
        }
    }

    private fun getKeyboardViewForType(keyboardType: KeyboardType) = when (keyboardType) {
        KeyboardType.QWERTY -> {
            qwertyKeyboardView
        }
        KeyboardType.IAST -> {
            iastKeyboardView
        }
        KeyboardType.SA -> {
            sansKeyboardView
        }
    }

    fun switchKeyBoard() {
        updateKeyboardType(when (keyboardType) {
            KeyboardType.IAST -> {
                KeyboardType.SA
            }
            KeyboardType.SA -> {
                KeyboardType.QWERTY
            }
            else -> {
                KeyboardType.IAST
            }
        })
    }
    // endregion

    // region suggestions
    private val suggestionsViewModel = SuggestionViewModel() // todo use injection
    private val _showSuggestions: MutableLiveData<Boolean> = MutableLiveData()
    private val liveDataShowSuggestions: MutableLiveData<Boolean>
        get() = _showSuggestions

    fun removeSuggestions() {
        val handler = Handler(mainLooper)
        handler.post {
            keyboardView.showSuggestions()
        }
    }

    fun updateSuggestions() {
        if (keyboardView.isSuggestionsOn) {
            val prefix = inputConnectionWrapper?.getBeforeCursorInSurroundingWord() ?: ""
            updateSuggestions(prefix)
        }
    }

    @SuppressLint("CheckResult")
    private fun updateSuggestions(string: String) {
        if (string.isEmpty()) {
            removeSuggestions()
        } else {
            suggestionsViewModel.getSuggestions(string).subscribe({
                if (it.isEmpty()) {
                    removeSuggestions()
                } else {
                    keyboardView.showSuggestions(it.take(8))
                }
            }, {
                Log.d("ZombieIMS", it.message ?: "No exception")
            })
        }
    }

    fun completeText(suggestion: String) {
        if (suggestion.isNotEmpty()) {
            // delete current word
            inputConnectionWrapper?.deleteSurroundingText()
            // commit new word
            inputConnectionWrapper?.commitText(suggestion)
            // auto-append space (if no word after)
            if (inputConnectionWrapper?.isLastWord() == true) {
                addSpace()
            }
        }
        // clear suggestions
        removeSuggestions()
    }

    private fun addSpace() {
        inputConnectionWrapper?.commitText(" ")
    }
    // endregion

    // region overrides
    override fun onCreateInputMethodSessionInterface(): AbstractInputMethodSessionImpl {
        Log.d(LOG_TAG, "onCreateInputMethodSessionInterface()")

        inputConnectionWrapper = InputConnectionWrapper2()

        val context = keyboardApplication

        qwertyKeyboardView = View.inflate(context, R.layout.custom_keyboard_view_qwerty, null) as QwertyKeyboardView
        qwertyKeyboardView.isSuggestionsOn = showsSuggestionsQwerty

        iastKeyboardView = View.inflate(context, R.layout.custom_keyboard_view_iast, null) as IastKeyboardView
        iastKeyboardView.isSuggestionsOn = showsSuggestionsIast

        sansKeyboardView = View.inflate(context, R.layout.custom_keyboard_view_sans, null) as SanskritKeyboardView
        sansKeyboardView.isSuggestionsOn = showsSuggestionsSans

        liveDataShowSuggestions.observe(this, Observer {
            keyboardView.isSuggestionsOn = it
        })

        return super.onCreateInputMethodSessionInterface()
    }

    override fun onBindInput() {
        Log.d(LOG_TAG, "onBindInput()")
        super.onBindInput()
        inputConnectionWrapper?.ic = currentInputConnection
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInput() hint: ${attribute?.hintText}")

        inputConnectionWrapper?.ic = currentInputConnection
        inputConnectionWrapper?.ei = attribute

        injector.ims = this
        keyboardType = when (inputConnectionWrapper?.imeActionLabel) {
            "iast" -> KeyboardType.IAST
            "sanskrit" -> KeyboardType.SA
            else -> KeyboardType.QWERTY
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInputView() keyboardView: $keyboardView")
        setInputView(keyboardView)
        updateSuggestions()
        // todo update action label
        val res = getIconRes()
        keyboardView.actionLabel = res
        setAutoCap()
    }

    override fun onCreateInputView(): View {
        Log.d(LOG_TAG, "onCreateInputView()")
        return keyboardView
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        Log.d(LOG_TAG, "onUpdateSelection()")
        inputConnectionWrapper?.updateSelection(newSelStart, newSelEnd, oldSelStart, oldSelEnd)
        updateSuggestions()
        setAutoCap()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(LOG_TAG, "onConfigurationChanged()")
        // todo
    }

    override fun getLifecycle(): Lifecycle {
        return ProcessLifecycleOwner.get().lifecycle
    }
    // endregion

    // region caps
    fun setAutoCap() {
        if (isQwerty()) {
            if (autoCapsOnFirstLetter && inputConnectionWrapper?.isBeginningOfSentence() == true) {
                keyboardView.shiftKeyView?.setPressedUI(true)
                setCapsOn()
            } else {
                keyboardView.shiftKeyView?.setPressedUI(false)
                setCapsOff()
            }
        }
    }

    private fun isQwerty(): Boolean {
        return keyboardType == KeyboardType.QWERTY
    }

    fun setCapsOn() {
        keyboardView.shiftableKeyViews.forEach {
            it.shiftKey(true)
        }
    }

    fun setCapsOff() {
        keyboardView.shiftableKeyViews.forEach {
            it.shiftKey(false)
        }
    }

    fun setCapsOffIfNotToggled() {
        val shiftKeyView = keyboardView.shiftKeyView
        if (shiftKeyView?.isPressedUI() == true && !shiftKeyView.isPersistent) {
            setCapsOff()
            shiftKeyView.setPressedUI(false)
        }
    }
    // endregion

    // region toggles
    fun resetSymbolToggle() {
        val symbolKeyView = keyboardView.getSymbolToggleByPressed()
        // hide toggleDigits key and show back digits if (symbol not persistent)
        if (symbolKeyView?.isPersistent == false) {
            keyboardView.setSymbolTogglePressed(false, symbolKeyView)
            showDigits()
        }
    }

    fun showDigits() {
        keyboardView.apply {
            this.extraKeyViews.forEach {
                it.resetText()
                it.enable(true)
            }
        }
    }
    // endregion

    // region candidates
    fun updateCandidates(keyView: CandidatesKeyView) {
        val candidatesLabels = keyView.getCandidatesLabels()
        if (candidatesLabels.isEmpty()) {
            showDigits()
        }
        val candidatesViews = keyboardView.extraKeyViews
        var lastIndex = 0
        (candidatesLabels.indices).forEach {
            candidatesViews[it].apply {
                this.setText(candidatesLabels[it])
                if (this is ExtraIastShiftKeyView && this.isUpperCase) {
                    this.setText(candidatesLabels[it].toUpperCase(Locale.getDefault()))
                } else {
                    this.setText(candidatesLabels[it])
                }

                this.enable(true)
            }
            lastIndex++
        }
        // add place holders for the remaining keyViews
        (lastIndex until candidatesViews.size).forEach {
            candidatesViews[it].apply {
                this.setPlaceholder()
                this.enable(false)
            }
        }
    }
    // endregion

    // region settings
    fun goToSettings() {
        val context = keyboardApplication
        context.startActivity(SettingsActivity.intent(context))
    }
    // endregion

    // region action
    fun performAction() {
        val ei = inputConnectionWrapper?.ei
        if (ei?.actionId != 0) {
            inputConnectionWrapper?.ic?.performEditorAction(ei?.actionId ?: 0)
        } else if (ei.imeOptions and EditorInfo.IME_MASK_ACTION != EditorInfo.IME_ACTION_NONE) {
            inputConnectionWrapper?.ic?.performEditorAction(ei.imeOptions and EditorInfo.IME_MASK_ACTION)
        }
    }

    private fun getIconRes(): String =
            when (currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
                EditorInfo.IME_ACTION_NONE -> {
//                    R.drawable.ic_done_white_24dp
                    "IME_ACTION_NONE"
                }
                EditorInfo.IME_ACTION_GO -> {
//                    R.drawable.ic_send_white_24dp
                    "IME_ACTION_GO"
                }
                EditorInfo.IME_ACTION_SEARCH -> {
//                    R.drawable.ic_search_white_24dp
                    "IME_ACTION_SEARCH"
                }
                EditorInfo.IME_ACTION_SEND -> {
//                    R.drawable.ic_send_white_24dp
                    "IME_ACTION_SEND"
                }
                EditorInfo.IME_ACTION_NEXT -> {
//                    R.drawable.ic_navigate_next_white_24dp
                    "IME_ACTION_NEXT"
                }
                EditorInfo.IME_ACTION_DONE -> {
//                    R.drawable.ic_search_white_24dp
                    "IME_ACTION_DONE"
                }
                EditorInfo.IME_ACTION_PREVIOUS -> {
//                    R.drawable.ic_navigate_before_white_24dp
                    "IME_ACTION_PREVIOUS"
                }
                else -> {
//                    R.drawable.ic_done_white_24dp
                    "DEFAULT"
                }
            }
    // endregion

    // region wrappers
    fun commitText(s: String) {
        inputConnectionWrapper?.commitText(s)
    }

    fun deleteCurrentSelection() {
        inputConnectionWrapper?.deleteCurrentSelection()
    }
    // endregion

    companion object {
        // region settings todos
        var maxSuggestions = 10 // todo make it a setting

        var showsSuggestionsQwerty = true // todo idem
        var showsSuggestionsIast = false // todo idem
        var showsSuggestionsSans = true // todo idem

        var autoCapsOnFirstLetter = true // todo idem
        // endregion

        // log tags
        private val LOG_TAG: String = "saiastkey"
        private val LOG_TAG_ISSUE: String = "kissue"
    }
}