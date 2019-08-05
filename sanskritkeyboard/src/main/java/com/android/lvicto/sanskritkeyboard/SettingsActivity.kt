package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, KeyboardSettingsFragment.instance())
                .commit()
    }

    companion object {
        fun intent(context: Context) : Intent = Intent(context, SettingsActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
        }
    }
}
