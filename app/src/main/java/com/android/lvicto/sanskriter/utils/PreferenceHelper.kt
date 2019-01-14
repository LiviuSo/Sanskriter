package com.android.lvicto.sanskriter.utils

import android.content.Context
import android.preference.PreferenceManager

class PreferenceHelper(private val context: Context) {

    companion object {
        private const val KEY_KEYBOARD_NAME = "keyboard_name"
        private const val KEY_KEYBOARD_PACKAGE = "keyboard_package"
        private const val KEY_KEYBOARD_SETUP = "keyboard_setup"
        private const val KEY_SECTION_TITLE = "section_title"
    }

    fun setKeyboardSelected(value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(KEY_KEYBOARD_SETUP, value).apply()
    }

    fun getKeyboardSelected(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_KEYBOARD_SETUP, false)
    }

    fun setKeyboardName(value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_KEYBOARD_NAME, value).apply()
    }

    fun getKeyboardName(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_KEYBOARD_NAME, "")!!
    }

    fun setKeyboardPackage(value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_KEYBOARD_PACKAGE, value).apply()
    }

    fun getKeyboardPackage(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_KEYBOARD_PACKAGE, "")!!
    }

    fun setLastSection(value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_SECTION_TITLE, value).apply()
    }

    fun getLastSection(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_SECTION_TITLE, "")!!
    }
}