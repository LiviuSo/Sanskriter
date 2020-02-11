package com.android.lvicto.zombie.keyboard.viewmodel

import com.android.lvicto.zombie.keyboard.view.keyboard.QwertyKeyboardView

class QwertyKeyboardViewModel(keyboardView: QwertyKeyboardView) : KeyboardViewModel(keyboardView) {

    fun setCapsOn() {
        (keyboardView as QwertyKeyboardView).shiftableKeyViews.forEach {
            it.setCaps(true)
        }
    }

    fun setCapsOff() {
        (keyboardView as QwertyKeyboardView).shiftableKeyViews.forEach {
            it.setCaps(false)
        }
    }

    fun autoCapsOn() {
        // todo
    }
}