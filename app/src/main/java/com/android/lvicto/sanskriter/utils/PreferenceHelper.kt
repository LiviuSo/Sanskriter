package com.android.lvicto.sanskriter.utils

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import com.android.lvicto.sanskriter.MyApplication.Companion.application

@SuppressLint("ApplySharedPref")
class PreferenceHelper {

    companion object {
        private const val KEY_KEYBOARD_NAME = "keyboard_name"
        private const val KEY_KEYBOARD_PACKAGE = "keyboard_package"
        private const val KEY_KEYBOARD_SETUP: String = "keyboard_setup"

        fun setKeyboardSelected(value: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                    .edit().putBoolean(KEY_KEYBOARD_SETUP, value).commit()
        }

        fun getKeyboardSelected(): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                    .getBoolean(KEY_KEYBOARD_SETUP, false)
        }

        fun setKeyboardName(value: String) {
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                    .edit().putString(KEY_KEYBOARD_NAME, value).commit()
        }

        fun getKeyboardName(): String {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                    .getString(KEY_KEYBOARD_NAME, "")
        }

        fun setKeyboardPackage(value: String) {
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                    .edit().putString(KEY_KEYBOARD_PACKAGE, value).commit()
        }

        fun getKeyboardPackage(): String {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                    .getString(KEY_KEYBOARD_PACKAGE, "")
        }
    }
}