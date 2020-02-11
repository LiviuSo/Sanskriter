package com.android.lvicto.zombie.keyboard.activity

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.Injector
import com.android.lvicto.zombie.keyboard.KeyboardType
import com.android.lvicto.zombie.keyboard.service.ZombieInputMethodService
import kotlinx.android.synthetic.main.activity_custom_keyboard_view.*

class CustomKeyboardViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_keyboard_view)

        val injector = Injector.getInstance(this)

        injector.ims = ZombieInputMethodService()

        var targerView: EditText? = null

        editTextSanskrit.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                Log.d("ZombieIMS", "editTextSanskrit")
                targerView = editTextSanskrit
                injector.ims.targetView = targerView
            } else {
                targerView = null
            }
        }

        editTextIast.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                Log.d("ZombieIMS", "editTextIast")
                targerView = editTextIast
                injector.ims.targetView = targerView
            } else {
                targerView = null
            }
        }

        editTextEnglish.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                Log.d("ZombieIMS", "editTextEnglish")
                targerView = editTextEnglish
                injector.ims.targetView = targerView
            } else {
                targerView = null
            }
        }

        val keyboardViewSans = layoutInflater.inflate(R.layout.custom_keyboard_view_sans, keyboardViewHolderSans, true)
        injector.ims.addKeyboardViewSans(keyboardViewSans.findViewById(R.id.customKeyboardViewSans))

        val keyboardViewIast = layoutInflater.inflate(R.layout.custom_keyboard_view_iast, keyboardViewHolderIast, true)
        injector.ims.addKeyboardViewIast(keyboardViewIast.findViewById(R.id.customKeyboardViewIast))

        injector.ims.keyboardType.observe(this, Observer { type ->
            type?.let {
                if (it == KeyboardType.SA) {
                    keyboardViewHolderIast.visibility = View.GONE
                    keyboardViewHolderSans.visibility = View.VISIBLE
                } else { // default to QWERTY
                    keyboardViewHolderIast.visibility = View.VISIBLE
                    keyboardViewHolderSans.visibility = View.GONE
                }
            }
        })
    }
}