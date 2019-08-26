package com.android.lvicto.sanskritkeyboard.ui

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.sanskritkeyboard.R
import kotlinx.android.synthetic.main.activity_keyboard_test.*


class TestKeyboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard_test)

        val activityManager = getSystemService(Activity.ACTIVITY_SERVICE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val tasks = (activityManager as ActivityManager).appTasks
            tasks.forEach {
                val taskDescr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    it.taskInfo.taskDescription
                } else {
                    null
                }
                if(taskDescr != null) {
                    if(taskDescr.label != null) {
                        Log.d("TestKeyboardActivity", "taskDescr = ${taskDescr.label}")
                    }
                }
            }
        } else {
            // clear
        }
    }

}