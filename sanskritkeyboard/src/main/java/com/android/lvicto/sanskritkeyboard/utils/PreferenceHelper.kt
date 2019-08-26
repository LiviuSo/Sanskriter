package com.android.lvicto.sanskritkeyboard.utils

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager

@SuppressLint("ApplySharedPref")
class PreferenceHelper(private val context: Context) {

    companion object {
        private const val KEY_KEYBOARD_NAME = "keyboard_name"
        private const val KEY_KEYBOARD_PACKAGE = "keyboard_package"
        private const val KEY_KEYBOARD_SETUP: String = "keyboard_setup"
        private const val KEY_KEYBOARD_LANG: String = "keyboard_lang"
    }

    fun setKeyboardSelected(value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)  // todo fix deprecation
                .edit().putBoolean(KEY_KEYBOARD_SETUP, value).commit()
    }

    fun getKeyboardSelected(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_KEYBOARD_SETUP, false)
    }

    fun setKeyboardName(value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_KEYBOARD_NAME, value).commit()
    }

    fun getKeyboardName(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_KEYBOARD_NAME, "").toString()
    }

    fun setKeyboardPackage(value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_KEYBOARD_PACKAGE, value).commit()
    }

    fun getKeyboardPackage(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_KEYBOARD_PACKAGE, "").toString()
    }

    fun setKeyboardLang(value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_KEYBOARD_LANG, value).commit()
    }

    fun getKeyboardLang(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_KEYBOARD_LANG, "").toString()
    }

}