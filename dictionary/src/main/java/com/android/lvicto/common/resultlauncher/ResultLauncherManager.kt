package com.android.lvicto.common.resultlauncher

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

class ResultLauncherManager(val activity: AppCompatActivity) {

    private val launchers = HashMap<Class<*>, ActivityResultLauncher<Intent>>()

    fun getLauncher(resultReceiver: Class<*>): ActivityResultLauncher<Intent>? {
        return launchers[resultReceiver.javaClass]
    }

    fun registerLauncher(resultReceiver: Class<*>, resLauncher: ActivityResultLauncher<Intent>) {
        launchers[resultReceiver.javaClass] = resLauncher
    }

    fun unregisterLauncher(resultReceiver: Class<*>) {
        launchers.remove(resultReceiver)
    }

}
