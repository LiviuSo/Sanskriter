package com.android.lvicto.zombie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.android.lvicto.zombie.coroutines.CoroutinesActivity
import com.android.lvicto.zombie.customview.CustomGroupViewActivity
import com.android.lvicto.zombie.keyboard.activity.CustomKeyboardViewActivity
import com.android.lvicto.zombie.livedata.LiveDataTransActivity
import com.android.lvicto.zombie.popup.PopUpAutoDismissActivity
import com.android.lvicto.zombie.popup.PopUpPositioningActivity
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
            }
        }

        btnCoroutines.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CoroutinesActivity::class.java))
            }
        }

        btnCustomGroupView.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CustomGroupViewActivity::class.java))
            }
        }

        btnCustomKeyboardView.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CustomKeyboardViewActivity::class.java))
            }
        }

        btnLiveDataTransformations.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, LiveDataTransActivity::class.java))
            }
        }
    }
}
