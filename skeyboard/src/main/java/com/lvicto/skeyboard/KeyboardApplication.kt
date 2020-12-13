package com.lvicto.skeyboard

import android.app.Application

class KeyboardApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        keyboardApplication = this
    }

    companion object {
        lateinit var keyboardApplication: KeyboardApplication
    }

}
