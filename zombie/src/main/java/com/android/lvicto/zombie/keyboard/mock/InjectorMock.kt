package com.android.lvicto.zombie.keyboard.mock

import android.content.Context
import com.android.lvicto.zombie.keyboard.mock.service.ZombieInputMethodService

class InjectorMock private constructor(val context: Context) {

    companion object {
        private var injectorInstance: InjectorMock? = null
        fun getInstance(context: Context): InjectorMock {
            if (injectorInstance == null) {
                injectorInstance = InjectorMock(context)
            }
            return injectorInstance as InjectorMock
        }
    }

    // region IMS

    // mock ims
    private lateinit var _mockIms: ZombieInputMethodService
    var mockIms: ZombieInputMethodService
        get() {
            return _mockIms
        }
        set(value) {
            _mockIms = value
        }
    // endregion
}