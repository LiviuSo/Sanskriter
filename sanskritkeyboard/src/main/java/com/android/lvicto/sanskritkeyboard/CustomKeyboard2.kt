package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button

class CustomKeyboard2 : InputMethodService() {

    private lateinit var ic: InputConnection
    private var config: Int? = null
    private var allCaps: Boolean = false
    private lateinit var keyQ: Button
    private lateinit var keyW: Button
    private lateinit var keyE: Button
    private lateinit var keyR: Button
    private lateinit var keyT: Button
    private lateinit var keyY: Button
    private lateinit var keyU: Button
    private lateinit var keyI: Button
    private lateinit var keyO: Button
    private lateinit var keyP: Button
    private lateinit var keyA: Button
    private lateinit var keyS: Button
    private lateinit var keyD: Button
    private lateinit var keyF: Button
    private lateinit var keyG: Button
    private lateinit var keyH: Button
    private lateinit var keyJ: Button
    private lateinit var keyK: Button
    private lateinit var keyL: Button
    private lateinit var keyZ: Button
    private lateinit var keyX: Button
    private lateinit var keyC: Button
    private lateinit var keyV: Button
    private lateinit var keyB: Button
    private lateinit var keyN: Button
    private lateinit var keyM: Button


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
                    initLetters(view)
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

    private val keyClickListener = View.OnClickListener {
        ic.commitText((it as Button).text, 1)
    }

    private fun initDigits(view: View) {
        view.findViewById<Button>(R.id.keyDigit1).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit2).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit3).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit4).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit5).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit6).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit7).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit8).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit9).apply {
            setOnClickListener(keyClickListener)
        }
        view.findViewById<Button>(R.id.keyDigit0).apply {
            setOnClickListener(keyClickListener)
        }
    }

    private fun initLetters(view: View) {
        keyQ = view.findViewById(R.id.keyQ)
        keyQ.apply { setOnClickListener(keyClickListener) }
        keyW = view.findViewById(R.id.keyW)
        keyW.apply { setOnClickListener(keyClickListener) }
        keyE = view.findViewById(R.id.keyE)
        keyE.apply { setOnClickListener(keyClickListener) }
        keyR = view.findViewById(R.id.keyR)
        keyR.apply { setOnClickListener(keyClickListener) }
        keyT = view.findViewById(R.id.keyT)
        keyT.apply { setOnClickListener(keyClickListener) }
        keyY = view.findViewById(R.id.keyY)
        keyY.apply { setOnClickListener(keyClickListener) }
        keyU = view.findViewById(R.id.keyU)
        keyU.apply { setOnClickListener(keyClickListener) }
        keyI = view.findViewById(R.id.keyI)
        keyI.apply { setOnClickListener(keyClickListener) }
        keyO = view.findViewById(R.id.keyO)
        keyO.apply { setOnClickListener(keyClickListener) }
        keyP = view.findViewById(R.id.keyP)
        keyP.apply { setOnClickListener(keyClickListener) }
        keyA = view.findViewById(R.id.keyA)
        keyA.apply { setOnClickListener(keyClickListener) }
        keyS = view.findViewById(R.id.keyS)
        keyS.apply { setOnClickListener(keyClickListener) }
        keyD = view.findViewById(R.id.keyD)
        keyD.apply { setOnClickListener(keyClickListener) }
        keyF = view.findViewById(R.id.keyF)
        keyF.apply { setOnClickListener(keyClickListener) }
        keyG = view.findViewById(R.id.keyG)
        keyG.apply { setOnClickListener(keyClickListener) }
        keyH = view.findViewById(R.id.keyH)
        keyH.apply { setOnClickListener(keyClickListener) }
        keyJ = view.findViewById(R.id.keyJ)
        keyJ.apply { setOnClickListener(keyClickListener) }
        keyK = view.findViewById(R.id.keyK)
        keyK.apply { setOnClickListener(keyClickListener) }
        keyL = view.findViewById(R.id.keyL)
        keyL.apply { setOnClickListener(keyClickListener) }
        view.findViewById<Button>(R.id.keyDel).apply {
            setOnClickListener {
                ic.deleteSurroundingText(1, 0)
            }
        }
        keyZ = view.findViewById(R.id.keyZ)
        keyZ.apply { setOnClickListener(keyClickListener) }
        keyX = view.findViewById(R.id.keyX)
        keyX.apply { setOnClickListener(keyClickListener) }
        keyC = view.findViewById(R.id.keyC)
        keyC.apply { setOnClickListener(keyClickListener) }
        keyV = view.findViewById(R.id.keyV)
        keyV.apply { setOnClickListener(keyClickListener) }
        keyB = view.findViewById(R.id.keyB)
        keyB.apply { setOnClickListener(keyClickListener) }
        keyN = view.findViewById(R.id.keyN)
        keyN.apply { setOnClickListener(keyClickListener) }
        keyM = view.findViewById(R.id.keyM)
        keyM.apply { setOnClickListener(keyClickListener) }

        // handles only ACTION_DONE for now
        view.findViewById<Button>(R.id.keyAction).apply {
            setOnClickListener {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
        }

        view.findViewById<Button>(R.id.keyShift).apply {
            setOnClickListener {
                allCaps = !allCaps
                shiftKeys(allCaps)
            }
        }
    }

    private fun shiftKeys(allCaps: Boolean) {
        keyQ.text = setCase(keyQ, allCaps)
        keyW.text = setCase(keyW, allCaps)
        keyE.text = setCase(keyE, allCaps)
        keyR.text = setCase(keyR, allCaps)
        keyT.text = setCase(keyT, allCaps)
        keyY.text = setCase(keyY, allCaps)
        keyU.text = setCase(keyU, allCaps)
        keyI.text = setCase(keyI, allCaps)
        keyO.text = setCase(keyO, allCaps)
        keyP.text = setCase(keyP, allCaps)
        keyA.text = setCase(keyA, allCaps)
        keyS.text = setCase(keyS, allCaps)
        keyD.text = setCase(keyD, allCaps)
        keyF.text = setCase(keyF, allCaps)
        keyG.text = setCase(keyG, allCaps)
        keyH.text = setCase(keyH, allCaps)
        keyJ.text = setCase(keyJ, allCaps)
        keyK.text = setCase(keyK, allCaps)
        keyL.text = setCase(keyL, allCaps)
        keyZ.text = setCase(keyZ, allCaps)
        keyX.text = setCase(keyX, allCaps)
        keyC.text = setCase(keyC, allCaps)
        keyV.text = setCase(keyV, allCaps)
        keyB.text = setCase(keyB, allCaps)
        keyN.text = setCase(keyN, allCaps)
        keyM.text = setCase(keyM, allCaps)
    }

    private fun setCase(button: Button, allCaps: Boolean): String = if(allCaps) {
        button.text.toString().toUpperCase()
    } else {
        button.text.toString().toLowerCase()
    }

    companion object {
        const val LOG_TAG = "CustomKeyboard2"
    }
}