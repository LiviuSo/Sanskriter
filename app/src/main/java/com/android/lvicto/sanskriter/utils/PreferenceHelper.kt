package com.android.lvicto.sanskriter.utils

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager

@SuppressLint("ApplySharedPref")
class PreferenceHelper(private val context: Context) {

    companion object {
        private const val KEY_KEYBOARD_NAME = "keyboard_name"
        private const val KEY_KEYBOARD_PACKAGE = "keyboard_package"
        private const val KEY_KEYBOARD_SETUP = "keyboard_setup"
        private const val KEY_LAST_ACCESSED_SECTION_TITLE = "keyboard_setup"
    }

    fun setKeyboardSelected(value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
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
                .getString(KEY_KEYBOARD_NAME, "")
    }

    fun setKeyboardPackage(value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_KEYBOARD_PACKAGE, value).commit()
    }

    fun getKeyboardPackage(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_KEYBOARD_PACKAGE, "")
    }

    fun setLastSection(section: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(KEY_LAST_ACCESSED_SECTION_TITLE, section).apply()
    }

    fun getLastSection(): String { // todo investigate exception
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LAST_ACCESSED_SECTION_TITLE, "")!!
    }
}