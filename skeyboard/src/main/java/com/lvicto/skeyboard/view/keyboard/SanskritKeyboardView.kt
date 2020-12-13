package com.lvicto.skeyboard.view.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.lvicto.skeyboard.KeyListeners
import com.lvicto.skeyboard.KeyboardType
import com.lvicto.skeyboard.view.key.BaseKeyView
import com.lvicto.skeyboard.view.key.ExtraKeyView
import com.lvicto.skeyboard.view.key.SanskritShiftKeyView
import com.lvicto.skeyboard.view.key.TypableKeyView

class SanskritKeyboardView(context: Context, attributeSet: AttributeSet)
    : CustomKeyboardView(context, attributeSet) {

    override fun setSpaceLabel(view: BaseKeyView) {
        view.text = KeyboardType.SA.name
    }

    override fun sieveView(view: View): Boolean {
        when (view) {
            is TypableKeyView -> {
                if (view !is ExtraKeyView) {
                    if (view is SanskritShiftKeyView) {
                        addShiftableKetView(view)
                    }
                    view.setOnTouchListener(KeyListeners.getKeyTypableTouchListener())
                    return true
                }
            }
        }
        return false
    }
}