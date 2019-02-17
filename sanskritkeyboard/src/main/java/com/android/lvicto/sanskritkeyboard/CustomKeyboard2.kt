package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button

class CustomKeyboard2 : InputMethodService() {

    private lateinit var ic: InputConnection

    companion object {
        const val LOG_TAG = "CustomKeyboard2"
    }

    private var config: Int? = null

    override fun onConfigureWindow(win: Window?, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        Log.d(LOG_TAG, "onConfigureWindow()")
        config = win?.context?.resources?.configuration?.orientation
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInputView()")
        ic = currentInputConnection
        super.onStartInputView(info, restarting)
    }

    override fun onCreateInputView(): View {
        Log.d(LOG_TAG, "onCreateInputView()")
        var view: View? = null
        if (!isTablet(context = applicationContext)) {
            when (config) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    view = layoutInflater.inflate(R.layout.keyboard_custom_layout_portrait, null)
                    initDigits(view)
                }
                Configuration.ORIENTATION_LANDSCAPE -> {
                    view = layoutInflater.inflate(R.layout.keyboard_custom_layout_landscape, null)
                }
                else -> {
                }
            }
        } else { // tablet
            when (config) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    view = layoutInflater.inflate(R.layout.keyboard_custom_layout_portrait_tablet, null)
                }
                Configuration.ORIENTATION_LANDSCAPE -> {
                    view = layoutInflater.inflate(R.layout.keyboard_custom_layout_landscape_tablet, null)
                }
                else -> {
                }
            }
        }

        return view!!
    }

    override fun onCreateCandidatesView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_custom_candidates, null)
        Log.d(LOG_TAG, "onCreateCandidatesView()")
        return view
    }

    override fun onCreateExtractTextView(): View {
        Log.d(LOG_TAG, "onCreateExtractTextView()")
        return super.onCreateExtractTextView()
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        Log.d(LOG_TAG, "onStartInput()")
        super.onStartInput(attribute, restarting)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        Log.d(LOG_TAG, "onConfigurationChanged()")
        when {
            newConfig!!.orientation == Configuration.ORIENTATION_PORTRAIT -> {
                Log.d(LOG_TAG, "onConfigurationChanged() : ORIENTATION_PORTRAIT")
                config = Configuration.ORIENTATION_PORTRAIT
            }
            newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE -> {
                Log.d(LOG_TAG, "onConfigurationChanged() : ORIENTATION_LANDSCAPE")
                config = Configuration.ORIENTATION_LANDSCAPE
            }
            else -> {
                Log.d(LOG_TAG, "onConfigurationChanged() : UNKNOWN")
            }
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        Log.d(LOG_TAG, "onCreate()")
        super.onCreate()
    }

    private fun isTablet(context: Context): Boolean {
        val xlarge = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == 4
        val large = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    private fun initDigits(view: View) {
        val b1 = view.findViewById<Button>(R.id.keyDigit1)
        val b2 = view.findViewById<Button>(R.id.keyDigit2)
        val b3 = view.findViewById<Button>(R.id.keyDigit3)
        val b4 = view.findViewById<Button>(R.id.keyDigit4)
        val b5 = view.findViewById<Button>(R.id.keyDigit5)
        val b6 = view.findViewById<Button>(R.id.keyDigit6)
        val b7 = view.findViewById<Button>(R.id.keyDigit7)
        val b8 = view.findViewById<Button>(R.id.keyDigit8)
        val b9 = view.findViewById<Button>(R.id.keyDigit9)
        val b0 = view.findViewById<Button>(R.id.keyDigit0)

        b1.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b2.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b3.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b4.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b5.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b6.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b7.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b8.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b9.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
        b0.setOnClickListener {
            ic.commitText((it as Button).text, 1)
        }
    }
}