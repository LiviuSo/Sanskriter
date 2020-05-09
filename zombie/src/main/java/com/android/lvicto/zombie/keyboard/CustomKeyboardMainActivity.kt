package com.android.lvicto.zombie.keyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.ims.activity.CustomKeyboardImsActivity
import com.android.lvicto.zombie.keyboard.mock.activity.CustomKeyboardMockActivity
import kotlinx.android.synthetic.main.activity_custom_keyboard_test.*

class CustomKeyboardMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_keyboard_test)

        buttonMockService.setOnClickListener {
            startActivity(Intent(this, CustomKeyboardMockActivity::class.java))
        }

        buttonIms.setOnClickListener {
            startActivity(Intent(this, CustomKeyboardImsActivity::class.java))
        }
    }
}