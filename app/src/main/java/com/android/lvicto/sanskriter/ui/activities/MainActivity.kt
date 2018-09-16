package com.android.lvicto.sanskriter.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.android.lvicto.sanskriter.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnTestBook).setOnClickListener {
            startActivity(Intent(this@MainActivity, BookActivity::class.java))
        }

        findViewById<Button>(R.id.btnTestDic).setOnClickListener {
            startActivity(Intent(this@MainActivity, DictionaryActivity::class.java))
        }

        findViewById<Button>(R.id.btnTestKeyboard).setOnClickListener {
            startActivity(Intent(this@MainActivity, TestKeyboardActivity::class.java))
        }

    }
}
