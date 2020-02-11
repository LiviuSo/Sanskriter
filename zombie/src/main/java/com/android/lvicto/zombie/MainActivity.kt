package com.android.lvicto.zombie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.android.lvicto.zombie.keyboard.activity.CustomKeyboardViewActivity
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

        val liveData = MutableLiveData<String>()
        liveData.value = null
        val transLiveData = Transformations.map(liveData) {
            it?.length
        }
        transLiveData.observe(this, Observer {
            Log.d("livedata", "$it")
        })

        val attr = intArrayOf()
        theme.obtainStyledAttributes(attr).apply {
            try {
                Log.d("attr", "${this.length()}")
            } finally {
                recycle()
            }
        }
    }
}
