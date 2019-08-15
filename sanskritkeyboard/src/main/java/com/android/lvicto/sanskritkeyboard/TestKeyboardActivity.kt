package com.android.lvicto.sanskritkeyboard

import android.app.Activity
import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


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
