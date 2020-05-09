package com.android.lvicto.zombie.keyboard.ims

import android.content.Context
import com.android.lvicto.zombie.keyboard.ims.service.ZombieCustomKeyboardIms

class Injector private constructor(val context: Context) {

    companion object {
        private var injectorInstance: Injector? = null
        fun getInstance(context: Context): Injector {
            if (injectorInstance == null) {
                injectorInstance = Injector(context)
            }
            return injectorInstance as Injector
        }
    }

    // region IMS
    private lateinit var _ims: ZombieCustomKeyboardIms
    var ims: ZombieCustomKeyboardIms
        get() {
            return _ims
        }
        set(value) {
            _ims = value
        }
    // endregion
}