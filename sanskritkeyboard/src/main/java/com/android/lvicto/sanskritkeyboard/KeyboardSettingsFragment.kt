package com.android.lvicto.sanskritkeyboard

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class KeyboardSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_keyboard, rootKey)
    }

    companion object {
        fun instance() : KeyboardSettingsFragment = KeyboardSettingsFragment()
    }
}