package com.lvicto.skeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.skeyboard.R
import kotlinx.android.synthetic.main.activity_new_keyboard.*

class NewKeyboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_keyboard)

        buttonEnableKeyboard.setOnClickListener {
            showInputMethodsManager(this)
        }

        buttonChooseKeyboard.setOnClickListener {
            chooseInputMethod()
        }
    }

    private fun showInputMethodsManager(activity: Activity) {
        val intent = Intent (Settings.ACTION_INPUT_METHOD_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivityForResult(intent, 0) // todo try to use startActivityForResult()
    }

    private fun chooseInputMethod() {
        val inputManager = application.applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showInputMethodPicker()
    }
}