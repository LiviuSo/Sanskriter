package com.android.lvicto.zombie.keyboard.view.keyboard

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.TouchListeners
import com.android.lvicto.zombie.keyboard.view.key.BaseKeyView
import com.android.lvicto.zombie.keyboard.view.key.ExtraKeyView
import com.android.lvicto.zombie.keyboard.view.key.ToggleKeyView
import com.android.lvicto.zombie.keyboard.view.key.TypableKeyView
import com.android.lvicto.zombie.keyboard.viewmodel.KeyboardViewModel

abstract class CustomKeyboardView(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        initKeyboard()
        (0 until childCount).forEach {
            getChildAt(it).let { view ->
                if (!sieveView(view)) {
                    sieveViewCommons(view)
                }
            }
        }
    }

    private fun sieveViewCommons(view: View) {
        view.apply {
            when (this) {
                is ExtraKeyView -> {
                    Log.d("sieveViewCommons", "ExtraKeyView: $mLabel")
                    addExtraKeyView(this)
                    this.setOnTouchListener(TouchListeners.keyExtraTouchListener)
                }
                is ToggleKeyView -> {
                    Log.d("sieveViewCommons", "ToggleKeyView")
                    if(context.resources.getInteger(R.integer.key_code_symbols) == this.mCode) {
                        symbolKeyView = this
                        this.setOnTouchListener(TouchListeners.keySymbolTouchListener)
                    }
                }
                is BaseKeyView -> {
                    Log.d("sieveViewCommons", "BaseKeyView")
                    when (this.mCode) {
                        context.resources.getInteger(R.integer.key_code_space) -> {
                            this.setOnTouchListener(TouchListeners.keySpaceTouchListener)
                        }
                        context.resources.getInteger(R.integer.key_code_ac) -> {
                            this.setOnTouchListener(TouchListeners.keyActionTouchListener)
                        }
                        context.resources.getInteger(R.integer.key_code_backspace) -> {
                            this.setOnTouchListener(TouchListeners.keyDeleteTouchListener)
                        }
                        context.resources.getInteger(R.integer.key_code_digits) -> {
                            toggleDigitsKeyView = this
                            this.setOnTouchListener(TouchListeners.keyToggleDigitsTouchListener)
                        }
                        else -> {
                            this.setOnTouchListener(TouchListeners.keySettingsTouchListener)
                        }
                    }
                }
                is LinearLayout -> { // suggestions // todo create a custom view
                    repeat((0 until this.childCount).count()) {
                        this.getChildAt(it).setOnTouchListener(TouchListeners.keySuggestionTouchListener)
                    }
                }
            }
        }
    }

    protected abstract fun sieveView(view: View): Boolean

    protected abstract fun initKeyboard()

    // region keyboardViewModel
    private var _keyboardViewModel: KeyboardViewModel? = null

    var keyboardViewModel: KeyboardViewModel
        get() = _keyboardViewModel!!
        set(value) {
            _keyboardViewModel = value
        }
    // endregion

    // region extras
    private val _extraViewModels = arrayListOf<TypableKeyView>()

    private fun addExtraKeyView(view: TypableKeyView) {
        _extraViewModels.add(view)
    }

    val extraKeyViews: List<TypableKeyView>
        get() = _extraViewModels.toList()
    // endregion

    // region toggleDigits
    private var _toggleDigitsKeyView: BaseKeyView? = null

    var toggleDigitsKeyView: BaseKeyView
        get() = _toggleDigitsKeyView!!
        set(value) {
            _toggleDigitsKeyView = value
        }
    // endregion

    // region symbols
    private var _symbolKeyView: ToggleKeyView? = null

    var symbolKeyView: ToggleKeyView
        get() = _symbolKeyView!!
        set(value) {
            _symbolKeyView = value
        }
    // endregion
}