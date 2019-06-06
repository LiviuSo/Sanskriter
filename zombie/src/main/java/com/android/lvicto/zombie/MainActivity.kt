package com.android.lvicto.zombie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EditText>(R.id.editSearch).setOnEditorActionListener { v, actionId, event ->
            Log.d(LOG_TAG, "$actionId $event")
            true
        }

        val view = View(this)
//        view.onCreateInputConnection()
        val w = window
//        w.addFlags()
//        this.getSystemService()
    }
}