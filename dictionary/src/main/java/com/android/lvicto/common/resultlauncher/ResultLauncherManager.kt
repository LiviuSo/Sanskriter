package com.android.lvicto.common.resultlauncher

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

class ResultLauncherManager(val activity: AppCompatActivity) {

    private val mLaunchers = HashMap<Class<*>, ActivityResultLauncher<Intent>>()

    fun getLauncher(resReceiver: Class<*>): ActivityResultLauncher<Intent>? {
        return mLaunchers[resReceiver.javaClass]
    }

    fun registerLauncher(resReceiver: Class<*>, resLauncher: ActivityResultLauncher<Intent>) {
        mLaunchers[resReceiver.javaClass] = resLauncher
    }

    fun unregisterLauncher(resReceiver: Class<*>) {
        mLaunchers.remove(resReceiver)
    }

}
