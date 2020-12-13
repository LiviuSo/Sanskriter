package com.lvicto.skeyboard

import android.content.Context
import com.lvicto.skeyboard.service.SanskritKeyboardIms

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
    private lateinit var _ims: SanskritKeyboardIms
    var ims: SanskritKeyboardIms
        get() {
            return _ims
        }
        set(value) {
            _ims = value
        }
    // endregion
}