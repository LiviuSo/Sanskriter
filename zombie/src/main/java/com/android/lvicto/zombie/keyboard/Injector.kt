package com.android.lvicto.zombie.keyboard

import android.content.Context
import com.android.lvicto.zombie.keyboard.service.ZombieInputMethodService
import com.android.lvicto.zombie.keyboard.view.keyboard.CustomKeyboardView
import com.android.lvicto.zombie.keyboard.view.keyboard.QwertyKeyboardView
import com.android.lvicto.zombie.keyboard.view.keyboard.SanskritKeyboardView

class Injector private constructor(val context: Context) {

    companion object {
        private var _instance: Injector? = null
        fun getInstance(context: Context): Injector {
            if (_instance == null) {
                _instance = Injector(context)
            }
            return _instance as Injector
        }
    }

    // region IMS
    private lateinit var _ims: ZombieInputMethodService
    var ims: ZombieInputMethodService
        get() {
            return _ims
        }
        set(value) {
            _ims = value
        }
    // endregion
}