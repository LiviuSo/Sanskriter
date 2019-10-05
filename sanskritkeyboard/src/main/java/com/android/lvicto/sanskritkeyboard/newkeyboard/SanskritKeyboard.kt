package com.android.lvicto.sanskritkeyboard.newkeyboard

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageButton
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType
import com.android.lvicto.sanskritkeyboard.newkeyboard.component.KeyboardKey
import com.android.lvicto.sanskritkeyboard.newkeyboard.component.ServiceWrapper
import com.android.lvicto.sanskritkeyboard.newkeyboard.component.TouchListeners
import com.android.lvicto.sanskritkeyboard.newkeyboard.helpers.*
import com.android.lvicto.sanskritkeyboard.service.SanskritCustomKeyboard.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.ui.SettingsActivity
import com.android.lvicto.sanskritkeyboard.utils.*

class SanskritKeyboard(val context: Context) {

    private val autoAddSpace: Boolean = true // todo get from settings
    private lateinit var serviceWrapper: ServiceWrapper
    private var actionKeyView: ImageButton? = null
    private lateinit var toggleDigitsKeyView: ImageButton
    private lateinit var shiftKeyView: ImageButton
    private lateinit var candidatesKeyViews: ArrayList<Button>

    private lateinit var digitKeyCodes: List<Int>
    private lateinit var keys: Map<Int, KeyboardKey> // code -> KeyboardKey
    private lateinit var candidatesCodes: Map<Int, List<Int>> // code -> list of codes

    private lateinit var keysFactoryHelper: KeysFactoryHelper
    private lateinit var suggestionHelper: SuggestionHelper
    private lateinit var toggleHelper: ToggleHelper
    private lateinit var capsStatusHelper: CapsStatusHelper
    private lateinit var config: KeyboardConfig


    /**
     * Bind with the input method service
     */
    fun bindIms(icw: ServiceWrapper) {
        serviceWrapper = icw
    }

    /**
     * Initialize the keyboard utilities use to construct it
     */
    fun initKeyboardUtils(config: KeyboardConfig) {
        this.config = config
        if (config.type == KeyboardType.QWERTY) {
            capsStatusHelper = CapsStatusHelper()
        }
        keysFactoryHelper = KeysFactoryHelper(context)
        suggestionHelper = SuggestionHelper()
        toggleHelper = ToggleHelper()
        keys = keysFactoryHelper.getKeys(config)
        candidatesCodes = keysFactoryHelper.getCandidatesMap(config)
        digitKeyCodes = keysFactoryHelper.getDigitsCodes(config)
    }

    /**
     * Create the keyboard
     * must be called after onBind() in IMS
     */
    fun buildKeyboard(): View {
        Log.d(LOG_TAG, "SanskritKeyboard : buildKeyboard()")
        val layoutRes = chooseLayout(config)
        val view = context.layoutInflater().inflate(layoutRes, null)

        initKeys(view, config)
        return view
    }

    /*
     * Switch the Sanskrit or qwerty
     */
    private fun chooseLayout(config: KeyboardConfig): Int {
        return if (config.type == KeyboardType.QWERTY) {
            R.layout.keyboard_phone_qwerty_portrait
        } else {
            R.layout.keyboard_phone_sanskrit_portrait
        }
    }

    private fun initKeys(view: View, config: KeyboardConfig) {
        val touchListeners = TouchListeners()
        bindKeys(view, config, touchListeners)
        initExtraKeys(view, touchListeners)
    }

    /*
     * keys, codesToIds must be init already
     */
    private fun bindKeys(view: View, config: KeyboardConfig, touchListeners: TouchListeners) {
        // inject the keyboard
        keys.keys.forEach { code ->
            (keys[code]
                    ?: error("Failed init keyboard; empty key in SanskritKeyboard.bindKeys()")).keyboard = this
        }

        // commons
        (view button R.id.keySpace).apply {
            this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keySpace, config)]
            setOnTouchListener(touchListeners.keySpaceTouchListener)
        }
        view.findViewById<ImageButton>(R.id.keyDel).apply {
            this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keyDel, config)]
            setOnTouchListener(touchListeners.keyDeleteTouchListener)
        }
        actionKeyView = (view imageButton R.id.keyAction).apply {
            this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keyAction, config)]
            setOnTouchListener(touchListeners.keyActionTouchListener)
        }
        (view button R.id.keySettings).apply {
            this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keySettings, config)]
            setOnTouchListener(touchListeners.settingsTouchListener)
        }
        // bindIms suggestions
        suggestionHelper.mSugg1 = (view button R.id.keySuggestion1).apply {
            this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keySuggestion1, config)]
            setOnTouchListener(touchListeners.keySuggestionTouchListener)
        }
        suggestionHelper.mSugg2 = (view button R.id.keySuggestion2).apply {
            this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keySuggestion2, config)]
            setOnTouchListener(touchListeners.keySuggestionTouchListener)
        }
        suggestionHelper.mSugg3 = (view button R.id.keySuggestion3).apply {
            this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keySuggestion3, config)]
            setOnTouchListener(touchListeners.keySuggestionTouchListener)
        }
        // particular
        when {
            config.type == KeyboardType.QWERTY -> { // QWERTY
                val keysClickListenerOnlyAllCaps = arrayListOf(
                        view button R.id.keyQ
                        , view button R.id.keyW
                        , view button R.id.keyE
                        , view button R.id.keyY
                        , view button R.id.keyO
                        , view button R.id.keyP
                        , view button R.id.keyF
                        , view button R.id.keyZ
                        , view button R.id.keyX
                        , view button R.id.keyV
                        , view button R.id.keyB
                        , view button R.id.keyJ
                        , view button R.id.keyK
                        , view button R.id.keyG
                        , view button R.id.keyR
                        , view button R.id.keyT
                        , view button R.id.keyU
                        , view button R.id.keyI
                        , view button R.id.keyA
                        , view button R.id.keyS
                        , view button R.id.keyD
                        , view button R.id.keyH
                        , view button R.id.keyL
                        , view button R.id.keyN
                        , view button R.id.keyM
                        , view button R.id.keyC
                )
                keysClickListenerOnlyAllCaps.forEach {
                    it.tag = keys[keysFactoryHelper.getCodeFromId(it.id, config)]
                    it.setOnTouchListener(touchListeners.keyQwertyTouchListener)
                }

                // todo different state for no sticky
                shiftKeyView = (view imageButton R.id.keyShift).apply {
                    this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keyShift, config)]
                    setOnTouchListener(touchListeners.keyShiftTouchListener)
                }

                view.findViewById<Button>(R.id.keyPunctuation).apply {
                    this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keyPunctuation, config)]
                    setOnTouchListener(touchListeners.keySymbolTouchListener)
                }
                view.findViewById<Button>(R.id.keyBraces).apply {
                    this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keyBraces, config)]
                    setOnTouchListener(touchListeners.keySymbolTouchListener)
                }
                view.findViewById<Button>(R.id.keySymbol).apply {
                    this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keySymbol, config)]
                    setOnTouchListener(touchListeners.keySymbolTouchListener)
                }
            }
            config.type == KeyboardType.SA -> { // SANSKRIT
                val keysClickListener = arrayListOf(
                        view button R.id.keySaA
                        , view button R.id.keySaI
                        , view button R.id.keySaU
                        , view button R.id.keySaRi
                        , view button R.id.keySaLri
                        , view button R.id.keySaE
                        , view button R.id.keySaO
                        , view button R.id.keySaKa
                        , view button R.id.keySaKha
                        , view button R.id.keySaGa
                        , view button R.id.keySaGha
                        , view button R.id.keySaCa
                        , view button R.id.keySaCha
                        , view button R.id.keySaJa
                        , view button R.id.keySaJha
                        , view button R.id.keySaTa2
                        , view button R.id.keySaTha2
                        , view button R.id.keySaDa2
                        , view button R.id.keySaDha2
                        , view button R.id.keySaTa
                        , view button R.id.keySaTha
                        , view button R.id.keySaDa
                        , view button R.id.keySaDha
                        , view button R.id.keySaPa
                        , view button R.id.keySaPha
                        , view button R.id.keySaBa
                        , view button R.id.keySaBha
                        , view button R.id.keySaNa1
                        , view button R.id.keySaNa2
                        , view button R.id.keySaNa3
                        , view button R.id.keySaNa4
                        , view button R.id.keySaMa
                        , view button R.id.keySaSa1
                        , view button R.id.keySaSa2
                        , view button R.id.keySaSa3
                        , view button R.id.keySaYa
                        , view button R.id.keySaRa
                        , view button R.id.keySaLa
                        , view button R.id.keySaVa
                        , view button R.id.keySaHa
                        , view button R.id.keySaSep
                        , view button R.id.keySaApostrophy
                )

                keysClickListener.forEach {
                    it.tag = keys[keysFactoryHelper.getCodeFromId(it.id, config)]
                    it.setOnTouchListener(touchListeners.keySansTouchListener)
                }

                (view button R.id.keySymbol).apply {
                    this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keySymbol, config)]
                    setOnTouchListener(touchListeners.keySymbolTouchListener)
                }
            }
            else -> {
                // error
            }
        }
    }

    /*
     * Init the candidates bar
     */
    private fun initExtraKeys(view: View, touchListeners: TouchListeners) {
        toggleDigitsKeyView = (view imageButton R.id.keyToggleDigits).apply {
            this.tag = keys[keysFactoryHelper.getCodeFromId(R.id.keyToggleDigits, config)]
            setOnTouchListener(touchListeners.keyToggleDigitsTouchListener)
        }
        candidatesKeyViews = arrayListOf<Button>().apply {
            addAll(arrayListOf(
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
        }
        candidatesKeyViews.forEach {
            it.setOnTouchListener(touchListeners.keyExtraTouchListener)
        }
        updateCandidates()
    }

    /**
     * Show digits in extras if key is null or the candidates of the key
     */
    fun updateCandidates(key: KeyboardKey? = null) {
        val candidatesCodes = (key?.getCandidates() ?: digitKeyCodes) ?: digitKeyCodes
        toggleDigitsKeyView.visibility = if (candidatesCodes != digitKeyCodes) {
            View.VISIBLE
        } else {
            View.GONE
        }

        Log.d(LOG_TAG, "updateCandidates(): ${key?.code} $candidatesCodes")
        var lastIndex = 0
        (candidatesCodes.indices).forEach {
            candidatesKeyViews[it].apply {
                tag = keys[candidatesCodes[it]]
                text = candidatesCodes[it].toChar().toString()
                isEnabled = true
            }
            lastIndex++
        }
        // reset the rest of the candidate keys
        (lastIndex until candidatesKeyViews.size).forEach {
            candidatesKeyViews[it].apply {
                tag = null
                text = R.string.key_label_placeholder.getString(context)
                isEnabled = false
            }
        }
    }

    /**
     * Call after onStartInput() in IMS
     */
    fun setActionIcon() {
        actionKeyView?.setImageResource(getIconRes())
    }

    private fun getIconRes(): Int =
            when (serviceWrapper.ei.imeOptions and EditorInfo.IME_MASK_ACTION) {
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

    /**
     * Open Settings activity
     */
    fun goToSettings() {
        context.startActivity(SettingsActivity.intent(context))
    }

    /**
     * Vibrate (on key press)
     */
    fun vibrate(long: Boolean = false) {
        context.vibrate(long)
    }

    /* method wrappers */

    /**
     * Action key functionality
     */
    fun performAction() {
        val ei = serviceWrapper.ei
        if (ei.actionId != 0) {
            serviceWrapper.performEditorAction(ei.actionId)
        } else if (ei.imeOptions and EditorInfo.IME_MASK_ACTION != EditorInfo.IME_ACTION_NONE) {
            serviceWrapper.performEditorAction(ei.imeOptions and EditorInfo.IME_MASK_ACTION)
        }
    }

    /**
     * Common key main functionality
     */
    fun commitText(text: String) {
        serviceWrapper.commitText(text, 1)
    }

    /**
     * Delete key main functionality
     */
    fun deleteCurrentSelection() {
        serviceWrapper.deleteCurrentSelection()
    }

    fun completeText(suggestion: String) { // todo fix logic
        if (suggestion.isNotEmpty()) {
            if (!suggestionHelper.justAddSuggestions) {
                // delete current word
                deleteSurroundingText()
            } else {
                // add space
                addSpace()
            }
            // commit new word
            commitText(suggestion)

            // auto-append space
            addSpace()
        }
        // clear suggestions
        suggestionHelper.clearSuggestions()
    }

    /*
     * Delete the word around the current position
     */
    private fun deleteSurroundingText() {
        val lenBefore = serviceWrapper.getBeforeCursorInSurroundingWord().length
        val lengthAfter = serviceWrapper.getAfterCursorInSurroundingWord().length
        serviceWrapper.deleteSurroundingText(lenBefore, lengthAfter)
    }

    /*
     * Auto-inserts a space after the suggestion
     */
    private fun addSpace() {
        if (autoAddSpace && !serviceWrapper.isLastWord()) {
            serviceWrapper.commitText(" ", 1)
        }
    }

    /**
     * Update suggestion
     */
    fun updateSuggestions() {
        // todo
    }

    /**
     * Delete all suggestions
     */
    fun removeSuggestions() {
        suggestionHelper.clearSuggestions()
    }

    /* toggle logic */
    fun resetToggled() {
        // todo use a broadcast
    }

    /* caps on logic */
    fun isCapsOn(): Boolean {
        return capsStatusHelper.isCapsOn
    }

    fun autoCapsOn() {
        // todo if punctuation and space before, then set caps on
        capsAllOn(on = true, toggled = false)
    }

    fun toggleCapsOn() {
        capsAllOn(on = true, toggled = true)
    }

    fun setCapsOff() {
        capsAllOn(false, toggled = false)
    }

    fun setCapsOn() {
        capsAllOn(true, toggled = false)
    }

    private fun capsAllOn(on: Boolean, toggled: Boolean) {
        capsStatusHelper.isCapsOn = on
        if(!on) {
            capsStatusHelper.isToggled = false // if set to off, reset toggled too
        } else {
            capsStatusHelper.isToggled = toggled
        }
        // todo for all qwerty key and current candidates - set caps
    }
}
