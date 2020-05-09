package com.android.lvicto.zombie.keyboard.mock.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.ims.KeyboardType
import com.android.lvicto.zombie.keyboard.mock.InjectorMock
import com.android.lvicto.zombie.keyboard.mock.service.ZombieInputMethodService
import kotlinx.android.synthetic.main.activity_custom_keyboard_view.*

/**
 * Just a test activity
 */
class CustomKeyboardMockActivity : AppCompatActivity() {

    private val injector = InjectorMock.getInstance(this).apply {
        this.mockIms = ZombieInputMethodService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_keyboard_view)
        injector.mockIms.onCreate(this)
    }

    override fun onStart() {
        super.onStart()

        var targetView: EditText?
        editTextQwerty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Log.d("ZombieIMS", "editTextEnglish")
                targetView = editTextQwerty
                injector.mockIms.targetView = targetView
            } else {
                targetView = null
            }
            injector.mockIms.removeSuggestions()
            hideKeyboard()
        }

        editTextIast.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Log.d("ZombieIMS", "editTextIAST")
                targetView = editTextIast
                injector.mockIms.targetView = targetView
            } else {
                targetView = null
            }
            injector.mockIms.removeSuggestions()
            hideKeyboard() // not working
        }

        editTextSanskrit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Log.d("ZombieIMS", "editTextSanskrit")
                targetView = editTextSanskrit
                injector.mockIms.targetView = targetView
            } else {
                targetView = null
            }
            injector.mockIms.removeSuggestions()
            hideKeyboard()
        }
    }

    fun showKeyboard(type: KeyboardType) {
        when (type) {
            KeyboardType.SA -> {
                keyboardViewHolderQwerty.visibility = View.GONE
                keyboardViewHolderIast.visibility = View.GONE
                keyboardViewHolderSans.visibility = View.VISIBLE
            }
            KeyboardType.IAST -> {
                keyboardViewHolderQwerty.visibility = View.GONE
                keyboardViewHolderIast.visibility = View.VISIBLE
                keyboardViewHolderSans.visibility = View.GONE
            }
            KeyboardType.QWERTY -> {  // default to QWERTY
                keyboardViewHolderQwerty.visibility = View.VISIBLE
                keyboardViewHolderIast.visibility = View.GONE
                keyboardViewHolderSans.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window.decorView.rootView.windowToken, 0)
    }
}