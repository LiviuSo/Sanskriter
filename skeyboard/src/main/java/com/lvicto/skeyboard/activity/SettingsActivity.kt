package com.lvicto.skeyboard.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lvicto.skeyboard.fragment.KeyboardSettingsFragment
import com.example.skeyboard.R

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
