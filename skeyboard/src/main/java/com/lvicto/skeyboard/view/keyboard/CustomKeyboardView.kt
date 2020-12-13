package com.lvicto.skeyboard.view.keyboard

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skeyboard.R
import com.lvicto.skeyboard.KeyListeners
import com.lvicto.skeyboard.data.Suggestion
import com.lvicto.skeyboard.data.SuggestionsAdapter
import com.lvicto.skeyboard.view.key.*
import kotlinx.android.synthetic.main.custom_keyboard_view_qwerty.view.*
import kotlinx.android.synthetic.main.row_suggestions.view.*

abstract class CustomKeyboardView(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr) {

    private var actionButton: BaseKeyView? = null
    private var _actionLabel: String = ""
    var actionLabel: String
        get() = _actionLabel
        set(value) {
            _actionLabel = value
            actionButton?.text = when(value) {
                "IME_ACTION_GO" -> "Go"
                "IME_ACTION_SEARCH" -> "Sc"
                "IME_ACTION_SEND" -> "Se"
                "IME_ACTION_DONE" -> "Dn"
                "IME_ACTION_NEXT" -> "Nx"
                "IME_ACTION_PREVIOUS" -> "Pr"
                else -> "N"
            }
        }

    private lateinit var recViewSuggestions: RecyclerView

    private var _isSuggestionsOn = true
    var isSuggestionsOn: Boolean
        get() {
            return _isSuggestionsOn
        }
        set(value) {
            _isSuggestionsOn = value
        }

    override fun onFinishInflate() {
        super.onFinishInflate()

        Log.d("sieveView", "llExtras")
        // init keyViews todo: move the key view
        (0 until childCount).forEach {
            getChildAt(it).let { view ->
                if (view.id == R.id.llExtras || view.id == R.id.llSuggestions) {
                    // sieve the extras
                    val extrasContainer = view as ViewGroup
                    (0 until extrasContainer.childCount).forEach { extra ->
                        val extraView = extrasContainer.getChildAt(extra)
                        if (!sieveView(extraView)) {
                            sieveViewCommons(extraView)
                        }
                    }
                } else {
                    if (!sieveView(view)) {
                        sieveViewCommons(view)
                    }
                }
            }
        }

        // init suggestion with blanks
        setupSuggestions()
    }

    private fun setupSuggestions() {
        val suggestionAdapter = SuggestionsAdapter(context).apply {
            data = SuggestionsAdapter.defaultSuggestions
        }
        recViewSuggestions = findViewById(R.id.rvSuggestions)
        recViewSuggestions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recViewSuggestions.adapter = suggestionAdapter
    }

    fun showSuggestions(data: List<Suggestion>? = null) {
        if (isSuggestionsOn) {
            if (data != null) {
                llSuggestions.visibility = View.VISIBLE
                recViewSuggestions.visibility = VISIBLE
                backSys.visibility = VISIBLE
                (recViewSuggestions.adapter as SuggestionsAdapter).data = data
            } else {
                recViewSuggestions.visibility = GONE
                backSys.visibility = GONE
            }
        }
    }

    private fun sieveViewCommons(view: View) {
        view.apply {
            when (this) {
                is ExtraKeyView -> {
                    Log.d("sieveView", "Custom: ExtraKeyView")
                    addExtraKeyView(this)
                    this.setOnTouchListener(KeyListeners.getKeyExtraTouchListener())
                }
                is ToggleKeyView -> {
                    Log.d("sieveView", "Custom: ToggleKeyView")
                    when (this.mCode) {
                        context.resources.getInteger(R.integer.key_code_iast_symbols) -> {
                            symbolKeyView = this
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols1),
                        context.resources.getInteger(R.integer.key_code_sans_symbols_short_vowels) -> {
                            symbolKeyView2 = this
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols2),
                        context.resources.getInteger(R.integer.key_code_sans_symbols_long_vowel) -> {
                            symbolKeyView3 = this
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols3),
                        context.resources.getInteger(R.integer.key_code_sans_symbols_short_diphtongs) -> {
                            symbolKeyView4 = this
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                        }
                        context.resources.getInteger(R.integer.key_code_sans_symbols_long_diphtongs) -> {
                            symbolKeyView5 = this
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                        }
                        context.resources.getInteger(R.integer.key_code_toggle_h),
                        context.resources.getInteger(R.integer.key_code_shift) -> {
                            shiftKeyView = this
                            this.setOnTouchListener(KeyListeners.getKeyShiftTouchListener())
                        }
                        else -> {
                            // nothing
                        }
                    }
                }
                is BaseKeyView -> {
                    when (this.mCode) {
                        context.resources.getInteger(R.integer.key_code_space) -> { // space
                            this.setOnTouchListener(KeyListeners.getKeySpaceTouchListener())
                            setSpaceLabel(this)
                        }
                        context.resources.getInteger(R.integer.key_code_ac) -> { // action
                            actionButton = this
                            this.setOnTouchListener(KeyListeners.getKeyActionTouchListener())
                        }
                        context.resources.getInteger(R.integer.key_code_backspace) -> { // delete
                            this.setOnTouchListener(KeyListeners.getKeyDeleteTouchListener())
                        }
                        context.resources.getInteger(R.integer.key_code_toggle_sys) -> { // toggle digits
                            this.setOnTouchListener(KeyListeners.getKeyToggleSystemBar())
                        }
                        else -> { // settings
                            this.setOnTouchListener(KeyListeners.getKeySettingsTouchListener())
                        }
                    }
                }
            }
        }
    }

    abstract fun setSpaceLabel(view: BaseKeyView)

    protected abstract fun sieveView(view: View): Boolean

    // region extras
    private val _extraViewModels = arrayListOf<TypableKeyView>()

    protected fun addExtraKeyView(view: TypableKeyView) {
        _extraViewModels.add(view)
    }

    fun setSymbolTogglePressed(pressed: Boolean, toggleView: ToggleKeyView?) {
        when (toggleView) {
            symbolKeyView -> {
                if(!pressed) {
                    symbolKeyView?.setPressedUI(false)
                    symbolKeyView?.setTogglePersistent(false)
                } else {
                    symbolKeyView?.setPressedUI(true)
                    symbolKeyView2?.setPressedUI(false)
                    symbolKeyView3?.setPressedUI(false)
                    symbolKeyView4?.setPressedUI(false)
                    symbolKeyView5?.setPressedUI(false)
                    // also reset the permanent toggle state
                    symbolKeyView?.setTogglePersistent(false)
                    symbolKeyView2?.setTogglePersistent(false)
                    symbolKeyView3?.setTogglePersistent(false)
                    symbolKeyView4?.setTogglePersistent(false)
                    symbolKeyView5?.setTogglePersistent(false)
                }
            }
            symbolKeyView2 -> {
                if (!pressed) {
                    symbolKeyView2?.setPressedUI(false)
                    symbolKeyView2?.setTogglePersistent(false)
                } else {
                    symbolKeyView2?.setPressedUI(true)
                    symbolKeyView?.setPressedUI(false)
                    symbolKeyView3?.setPressedUI(false)
                    symbolKeyView4?.setPressedUI(false)
                    symbolKeyView5?.setPressedUI(false)
                    // also reset the permanent toggle state
                    symbolKeyView?.setTogglePersistent(false)
                    symbolKeyView2?.setTogglePersistent(false)
                    symbolKeyView3?.setTogglePersistent(false)
                    symbolKeyView4?.setTogglePersistent(false)
                    symbolKeyView5?.setTogglePersistent(false)
                }
            }
            symbolKeyView3 -> {
                if (!pressed) {
                    symbolKeyView3?.setPressedUI(false)
                    symbolKeyView3?.setTogglePersistent(false)
                } else {
                    symbolKeyView3?.setPressedUI(true)
                    symbolKeyView2?.setPressedUI(false)
                    symbolKeyView?.setPressedUI(false)
                    symbolKeyView4?.setPressedUI(false)
                    symbolKeyView5?.setPressedUI(false)
                    // also reset the permanent toggle state
                    symbolKeyView?.setTogglePersistent(false)
                    symbolKeyView2?.setTogglePersistent(false)
                    symbolKeyView3?.setTogglePersistent(false)
                    symbolKeyView4?.setTogglePersistent(false)
                    symbolKeyView5?.setTogglePersistent(false)
                }
            }
            symbolKeyView4 -> {
                if (!pressed) {
                    symbolKeyView4?.setPressedUI(false)
                    symbolKeyView4?.setTogglePersistent(false)
                } else {
                    symbolKeyView4?.setPressedUI(true)
                    symbolKeyView2?.setPressedUI(false)
                    symbolKeyView3?.setPressedUI(false)
                    symbolKeyView?.setPressedUI(false)
                    symbolKeyView5?.setPressedUI(false)
                    // also reset the permanent toggle state
                    symbolKeyView?.setTogglePersistent(false)
                    symbolKeyView2?.setTogglePersistent(false)
                    symbolKeyView3?.setTogglePersistent(false)
                    symbolKeyView4?.setTogglePersistent(false)
                    symbolKeyView5?.setTogglePersistent(false)
                }
            }
            symbolKeyView5 -> {
                if (!pressed) {
                    symbolKeyView5?.setPressedUI(false)
                    symbolKeyView5?.setTogglePersistent(false)
                } else {
                    symbolKeyView5?.setPressedUI(true)
                    symbolKeyView2?.setPressedUI(false)
                    symbolKeyView3?.setPressedUI(false)
                    symbolKeyView4?.setPressedUI(false)
                    symbolKeyView?.setPressedUI(false)
                    // also reset the permanent toggle state
                    symbolKeyView?.setTogglePersistent(false)
                    symbolKeyView2?.setTogglePersistent(false)
                    symbolKeyView3?.setTogglePersistent(false)
                    symbolKeyView4?.setTogglePersistent(false)
                    symbolKeyView5?.setTogglePersistent(false)
                }
            }
        }
    }

    fun getSymbolToggleByPressed(): ToggleKeyView? {
        return if (symbolKeyView != null && symbolKeyView?.isPressedUI() == true) symbolKeyView
        else if (symbolKeyView2 != null && symbolKeyView2?.isPressedUI() == true) symbolKeyView2
        else if (symbolKeyView3 != null && symbolKeyView3?.isPressedUI() == true) symbolKeyView3
        else if (symbolKeyView4 != null && symbolKeyView4?.isPressedUI() == true) symbolKeyView4
        else if (symbolKeyView5 != null && symbolKeyView5?.isPressedUI() == true) symbolKeyView5
        else null
    }

    fun getSymbolToggleByCandidates(@ArrayRes candidates: Int): ToggleKeyView? {
        return if (symbolKeyView != null && symbolKeyView?.candidatesResId == candidates) symbolKeyView
        else if (symbolKeyView2 != null && symbolKeyView2?.candidatesResId == candidates) symbolKeyView2
        else if (symbolKeyView3 != null && symbolKeyView3?.candidatesResId == candidates) symbolKeyView3
        else if (symbolKeyView4 != null && symbolKeyView4?.candidatesResId == candidates) symbolKeyView4
        else if (symbolKeyView5 != null && symbolKeyView5?.candidatesResId == candidates) symbolKeyView5
        else null
    }

    val extraKeyViews: List<TypableKeyView>
        get() = _extraViewModels.toList()
    // endregion

    // region symbols
    private var _symbolKeyView: ToggleKeyView? = null
    private var _symbolKeyView2: ToggleKeyView? = null
    private var _symbolKeyView3: ToggleKeyView? = null
    private var _symbolKeyView4: ToggleKeyView? = null
    private var _symbolKeyView5: ToggleKeyView? = null

    var symbolKeyView: ToggleKeyView?
        get() = _symbolKeyView
        set(value) {
            _symbolKeyView = value
        }

    var symbolKeyView2: ToggleKeyView?
        get() = _symbolKeyView2
        set(value) {
            _symbolKeyView2 = value
        }

    var symbolKeyView3: ToggleKeyView?
        get() = _symbolKeyView3
        set(value) {
            _symbolKeyView3 = value
        }

    var symbolKeyView4: ToggleKeyView?
        get() = _symbolKeyView4
        set(value) {
            _symbolKeyView4 = value
        }

    var symbolKeyView5: ToggleKeyView?
        get() = _symbolKeyView5
        set(value) {
            _symbolKeyView5 = value
        }
    // endregion

    // region shift-ables
    private var _shiftableKeyViews = hashSetOf<ShiftKey>()

    protected fun addShiftableKetView(view: ShiftKey) {
        _shiftableKeyViews.add(view)
    }

    val shiftableKeyViews: List<ShiftKey>
        get() = _shiftableKeyViews.toList()
    // endregion

    // region shift
    private var _shiftKeyView: ToggleKeyView? = null
    var shiftKeyView: ToggleKeyView?
        get() = _shiftKeyView
        set(value) {
            _shiftKeyView = value
        }
    // endregion
}