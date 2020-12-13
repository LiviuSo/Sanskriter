package com.lvicto.skeyboard.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.skeyboard.R

class KeyboardSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_keyboard, rootKey)
    }

    companion object {
        fun instance() : KeyboardSettingsFragment = KeyboardSettingsFragment()
    }
}