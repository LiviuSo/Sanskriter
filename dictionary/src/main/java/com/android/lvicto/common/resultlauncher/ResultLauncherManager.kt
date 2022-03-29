package com.android.lvicto.common.resultlauncher

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

class ResultLauncherManager(val activity: AppCompatActivity) {

    private val mLaunchers = HashMap<Class<*>, ActivityResultLauncher<Intent>>()

    fun getLauncher(resultReceiver: Class<*>): ActivityResultLauncher<Intent>? {
        return mLaunchers[resultReceiver.javaClass]
    }

    fun registerLauncher(resultReceiver: Class<*>, resLauncher: ActivityResultLauncher<Intent>) {
        mLaunchers[resultReceiver.javaClass] = resLauncher
    }

    fun unregisterLauncher(resultReceiver: Class<*>) {
        mLaunchers.remove(resultReceiver)
    }

}
