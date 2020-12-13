package com.lvicto.skeyboard.view.keyboard

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.skeyboard.R
import com.lvicto.skeyboard.KeyListeners
import com.lvicto.skeyboard.KeyboardType
import com.lvicto.skeyboard.view.key.*

class QwertyKeyboardView(context: Context, attributeSet: AttributeSet)
    : CustomKeyboardView(context, attributeSet) {

    override fun setSpaceLabel(view: BaseKeyView) {
        view.text = KeyboardType.QWERTY.name
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
                    }
                }
                is ToggleKeyView -> {
                    return when (this.mCode) {
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols1) -> {
                            Log.d("sieveView", "QWERTY: key_code_qwerty_symbols1")
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                            symbolKeyView = this
                            true
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols2) -> {
                            Log.d("sieveView", "QWERTY: key_code_qwerty_symbols2")
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                            symbolKeyView2 = this
                            true
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols3) -> {
                            Log.d("sieveView", "QWERTY: key_code_qwerty_symbols3")
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                            symbolKeyView3 = this
                            true
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols4) -> {
                            Log.d("sieveView", "QWERTY: key_code_qwerty_symbols3")
                            this.setOnTouchListener(KeyListeners.getKeySymbolTouchListener())
                            symbolKeyView4 = this
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
}