package com.android.lvicto.zombie.keyboard.mock.keyboard

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.mock.KeyListenersMock
import com.android.lvicto.zombie.keyboard.ims.KeyboardType
import com.android.lvicto.zombie.keyboard.ims.view.key.*
import com.android.lvicto.zombie.keyboard.ims.view.keyboard.CustomKeyboardView

class QwertyKeyboardViewMock(context: Context, attributeSet: AttributeSet)
    : CustomKeyboardViewMock(context, attributeSet) {

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
                        this.setOnTouchListener(KeyListenersMock.getKeyTypableTouchListener())
                        return true
                    }
                }
                is ToggleKeyView -> {
                    return when (this.mCode) {
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols1) -> {
                            Log.d("sieveView", "QWERTY: key_code_qwerty_symbols1")
                            this.setOnTouchListener(KeyListenersMock.getKeySymbolTouchListener())
                            symbolKeyView = this
                            true
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols2) -> {
                            Log.d("sieveView", "QWERTY: key_code_qwerty_symbols2")
                            this.setOnTouchListener(KeyListenersMock.getKeySymbolTouchListener())
                            symbolKeyView2 = this
                            true
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols3) -> {
                            Log.d("sieveView", "QWERTY: key_code_qwerty_symbols3")
                            this.setOnTouchListener(KeyListenersMock.getKeySymbolTouchListener())
                            symbolKeyView3 = this
                            true
                        }
                        context.resources.getInteger(R.integer.key_code_qwerty_symbols4) -> {
                            Log.d("sieveView", "QWERTY: key_code_qwerty_symbols3")
                            this.setOnTouchListener(KeyListenersMock.getKeySymbolTouchListener())
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