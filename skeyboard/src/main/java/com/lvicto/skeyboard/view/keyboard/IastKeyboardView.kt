package com.lvicto.skeyboard.view.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.lvicto.skeyboard.KeyListeners
import com.lvicto.skeyboard.KeyboardType
import com.lvicto.skeyboard.view.key.BaseKeyView
import com.lvicto.skeyboard.view.key.ExtraKeyView
import com.lvicto.skeyboard.view.key.ShiftKey
import com.lvicto.skeyboard.view.key.TypableKeyView

class IastKeyboardView(context: Context, attributeSet: AttributeSet)
    : CustomKeyboardView(context, attributeSet) {

    override fun setSpaceLabel(view: BaseKeyView) {
        view.text = KeyboardType.IAST.name
    }

    override fun sieveView(view: View): Boolean {
        view.apply {
            when (this) {
                is TypableKeyView -> {
                    if (this !is ExtraKeyView) {
                        if (this is ShiftKey) {
                            addShiftableKetView(this)
                        }
                        this.setOnTouchListener(KeyListeners.getKeyTypableTouchListener())
                        return true
                    } else {
                        if (this is ShiftKey) { // is extra, but also shiftable
                            addShiftableKetView(this)
                            addExtraKeyView(this)
                            this.setOnTouchListener(KeyListeners.getKeyExtraTouchListener())
                            return true
                        }
                    }
                }
            }
            return false
        }
    }
}