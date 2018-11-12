package com.android.lvicto.zombie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EditText>(R.id.editSearch).setOnEditorActionListener { v, actionId, event ->
            Log.d(LOG_TAG, "$actionId $event")
            true
        }
    }
}