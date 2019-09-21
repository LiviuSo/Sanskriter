package com.android.lvicto.zombie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPopUpPositioning.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, PopUpPositioningActivity::class.java))
            }
        }

        btnPopUpAutoDismiss.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, PopUpAutoDismissActivity::class.java))
            }
        }

        btnKeyboardTest.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, TestKeyboardActivity::class.java))
            }
        }

        btnCoroutines.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CoroutineGuidesActivity::class.java))
            }
        }
    }
}
