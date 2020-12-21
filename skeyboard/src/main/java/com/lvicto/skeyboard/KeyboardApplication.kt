package com.lvicto.skeyboard

import android.app.Application
import android.content.res.Configuration

class KeyboardApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        keyboardApplication = this
    }

    companion object {
        lateinit var keyboardApplication: KeyboardApplication
    }

}

fun Application.isPortrait(): Boolean = this.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT