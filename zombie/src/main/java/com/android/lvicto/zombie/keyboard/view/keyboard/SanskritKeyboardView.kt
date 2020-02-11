package com.android.lvicto.zombie.keyboard.view.keyboard

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.android.lvicto.zombie.keyboard.TouchListeners
import com.android.lvicto.zombie.keyboard.view.key.SanskritKeyView
import com.android.lvicto.zombie.keyboard.view.key.TypableKeyView
import com.android.lvicto.zombie.keyboard.viewmodel.SanskritKeyboardViewModel

class SanskritKeyboardView(context: Context, attributeSet: AttributeSet)
    : CustomKeyboardView(context, attributeSet) {

    override fun initKeyboard() {
        keyboardViewModel = SanskritKeyboardViewModel(this)
    }

    override fun sieveView(view: View): Boolean {
        when(view) {
            is SanskritKeyView -> {
                Log.d("sieveView", "SanskritKeyView")
                view.setOnTouchListener(TouchListeners.keySansTouchListener)
                return true
            }
        }
        return false
    }
}