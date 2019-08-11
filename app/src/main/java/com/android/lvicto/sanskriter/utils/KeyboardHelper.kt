package com.android.lvicto.sanskriter.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.android.lvicto.sanskriter.MyApplication.Companion.application


object KeyboardHelper {
    private val inputManager = application.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private val LOG_TAG = this::class.java.simpleName


    fun sendToPlayStore(packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=$packageName")
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        application.startActivity(intent)
    }

    fun isSoftInputEnabled(name: String): Boolean {
        val enabledMethods = inputManager.enabledInputMethodList
        return enabledMethods.filter {
            Log.d(LOG_TAG, "installed: ${it.serviceInfo.name}\n")
            it.serviceInfo.name.contains(name)
        }.isNotEmpty()
    }

    fun showSoftInputMethodsSelector() {
        inputManager.showInputMethodPicker()
    }

    fun isDefaultInputMethod(name: String): Boolean = Settings.Secure.getString(application.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD).contains(name)

    fun softInputInstalled(packageName: String): Boolean {
        val pm: PackageManager = application.packageManager
        val pkgAppsList = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        return pkgAppsList.any {
            Log.d(LOG_TAG, it.packageName)
            it.packageName == packageName
        }
    }

    fun showInputMethodsManager(activity: Activity) {
        val intent = Intent (android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        activity.startActivityForResult(intent, 0) // todo try to use startActivityForResult()
    }
}