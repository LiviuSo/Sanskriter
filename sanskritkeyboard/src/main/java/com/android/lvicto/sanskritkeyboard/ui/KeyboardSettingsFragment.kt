package com.android.lvicto.sanskritkeyboard.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.android.lvicto.sanskritkeyboard.R

class KeyboardSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_keyboard, rootKey)
    }

    companion object {
        fun instance() : KeyboardSettingsFragment = KeyboardSettingsFragment()
    }
}