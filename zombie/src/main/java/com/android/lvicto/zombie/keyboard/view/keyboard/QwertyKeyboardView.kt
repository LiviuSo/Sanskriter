package com.android.lvicto.zombie.keyboard.view.keyboard

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.TouchListeners
import com.android.lvicto.zombie.keyboard.view.key.ShiftableKeyView
import com.android.lvicto.zombie.keyboard.view.key.ToggleKeyView
import com.android.lvicto.zombie.keyboard.viewmodel.QwertyKeyboardViewModel

class QwertyKeyboardView(context: Context, attributeSet: AttributeSet)
    : CustomKeyboardView(context, attributeSet) {

    override fun initKeyboard() {
        keyboardViewModel = QwertyKeyboardViewModel(this)
    }

    override fun sieveView(view: View): Boolean {
        view.apply {
            when (this) {
                is ShiftableKeyView -> {
                    Log.d("sieveView", "ShiftableKeyView")
                    addShiftableKetView(this)
                    this.setOnTouchListener(TouchListeners.keyQwertyTouchListener)
                    return true
                }
                is ToggleKeyView -> {
                    return when (this.mCode) {
                        context.resources.getInteger(R.integer.key_code_shift) -> {
                            Log.d("sieveView", "ToggleKeyView")
                            shiftKeyView = this
                            this.setOnTouchListener(TouchListeners.keyShiftTouchListener)
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            }
            return false
        }
    }

    // region shiftables
    private var _shiftableKeyViews = hashSetOf<ShiftableKeyView>()

    private fun addShiftableKetView(view: ShiftableKeyView) {
        _shiftableKeyViews.add(view)
    }

    val shiftableKeyViews: List<ShiftableKeyView>
        get() = _shiftableKeyViews.toList()
    // endregion

    // region shift
    private var _shiftKeyView: ToggleKeyView? = null

    var shiftKeyView: ToggleKeyView
        get() = _shiftKeyView!!
        set(value) {
            _shiftKeyView = value
        }
    // endregion

}