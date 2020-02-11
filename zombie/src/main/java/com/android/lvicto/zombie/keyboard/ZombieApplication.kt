package com.android.lvicto.zombie.keyboard

import android.app.Application
import com.android.lvicto.zombie.keyboard.service.ZombieInputMethodService

class ZombieApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        zombieApplication = this
    }

    companion object {
        lateinit var zombieApplication: ZombieApplication
    }

}
