package com.android.lvicto.zombie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class TestKeyboardActivity : AppCompatActivity() {

    private val LOG_TAG = "TestKeyboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard_test)

        findViewById<EditText>(R.id.editSearch).setOnEditorActionListener { v, actionId, event ->
            Log.d(LOG_TAG, "$actionId $event")
            true
        }
    }
}