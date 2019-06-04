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

    private lateinit var key0: Button
    private lateinit var key1: Button
    private lateinit var key2: Button
    private lateinit var key3: Button
    private lateinit var key4: Button
    private lateinit var key5: Button
    private lateinit var key6: Button
    private lateinit var key7: Button
    private lateinit var key8: Button
    private lateinit var key9: Button

    private lateinit var keyDel: Button
    private lateinit var keyAc: Button
    private lateinit var keyShift: Button

    private lateinit var keyQuestion: Button
    private lateinit var keyPeriod: Button
    private lateinit var keyComma: Button
    private lateinit var keyHyphen: Button
    private lateinit var keySpace: Button
    private lateinit var keySm: Button
    private lateinit var keyLa: Button
    private lateinit var keyIc: Button




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
                    initKeys(view)
                    setKeyOnClickListeners()
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

    private fun getKeyClickListener(text: String = "") = View.OnClickListener {
        val output = if(text.isEmpty()) {
            (it as Button).text
        } else {
            text
        }
        ic.commitText(output, output.length)
    }

    // todo : make a helper
    // todo : use ButterKnife
    private fun initKeys(view: View) {
        key1 = view.findViewById(R.id.keyDigit1)
        key2 = view.findViewById(R.id.keyDigit2)
        key3 = view.findViewById(R.id.keyDigit3)
        key4 = view.findViewById(R.id.keyDigit4)
        key5 = view.findViewById(R.id.keyDigit5)
        key6 = view.findViewById(R.id.keyDigit6)
        key7 = view.findViewById(R.id.keyDigit7)
        key8 = view.findViewById(R.id.keyDigit8)
        key9 = view.findViewById(R.id.keyDigit9)
        key0 = view.findViewById(R.id.keyDigit0)

        keyQ = view.findViewById(R.id.keyQ)
        keyW = view.findViewById(R.id.keyW)
        keyE = view.findViewById(R.id.keyE)
        keyR = view.findViewById(R.id.keyR)
        keyT = view.findViewById(R.id.keyT)
        keyY = view.findViewById(R.id.keyY)
        keyU = view.findViewById(R.id.keyU)
        keyI = view.findViewById(R.id.keyI)
        keyO = view.findViewById(R.id.keyO)
        keyP = view.findViewById(R.id.keyP)
        keyA = view.findViewById(R.id.keyA)
        keyS = view.findViewById(R.id.keyS)
        keyD = view.findViewById(R.id.keyD)
        keyF = view.findViewById(R.id.keyF)
        keyG = view.findViewById(R.id.keyG)
        keyH = view.findViewById(R.id.keyH)
        keyJ = view.findViewById(R.id.keyJ)
        keyK = view.findViewById(R.id.keyK)
        keyL = view.findViewById(R.id.keyL)
        keyZ = view.findViewById(R.id.keyZ)
        keyX = view.findViewById(R.id.keyX)
        keyC = view.findViewById(R.id.keyC)
        keyV = view.findViewById(R.id.keyV)
        keyB = view.findViewById(R.id.keyB)
        keyN = view.findViewById(R.id.keyN)
        keyM = view.findViewById(R.id.keyM)

        keyDel = view.findViewById(R.id.keyDel)
        keyAc = view.findViewById(R.id.keyAction)
        keyShift = view.findViewById(R.id.keyShift)

        keyQuestion = view.findViewById(R.id.keyQuestion)
        keyPeriod = view.findViewById(R.id.keyQuestion)
        keyComma = view.findViewById(R.id.keyComma)
        keyHyphen = view.findViewById(R.id.keyHyphen)
        keySpace = view.findViewById(R.id.keySpace)
        keySm = view.findViewById(R.id.keySym)
        keyLa = view.findViewById(R.id.keyLang)
        keyIc = view.findViewById(R.id.keyIcon)
    }

    private fun setKeyOnClickListeners() {
        key0.setOnClickListener(getKeyClickListener())
        key1.setOnClickListener(getKeyClickListener())
        key2.setOnClickListener(getKeyClickListener())
        key3.setOnClickListener(getKeyClickListener())
        key4.setOnClickListener(getKeyClickListener())
        key5.setOnClickListener(getKeyClickListener())
        key6.setOnClickListener(getKeyClickListener())
        key7.setOnClickListener(getKeyClickListener())
        key8.setOnClickListener(getKeyClickListener())
        key9.setOnClickListener(getKeyClickListener())

        keyQ.setOnClickListener(getKeyClickListener())
        keyW.setOnClickListener(getKeyClickListener())
        keyE.setOnClickListener(getKeyClickListener())
        keyR.setOnClickListener(getKeyClickListener())
        keyT.setOnClickListener(getKeyClickListener())
        keyY.setOnClickListener(getKeyClickListener())
        keyU.setOnClickListener(getKeyClickListener())
        keyI.setOnClickListener(getKeyClickListener())
        keyO.setOnClickListener(getKeyClickListener())
        keyP.setOnClickListener(getKeyClickListener())
        keyA.setOnClickListener(getKeyClickListener())
        keyS.setOnClickListener(getKeyClickListener())
        keyD.setOnClickListener(getKeyClickListener())
        keyF.setOnClickListener(getKeyClickListener())
        keyG.setOnClickListener(getKeyClickListener())
        keyH.setOnClickListener(getKeyClickListener())
        keyJ.setOnClickListener(getKeyClickListener())
        keyK.setOnClickListener(getKeyClickListener())
        keyL.setOnClickListener(getKeyClickListener())
        keyDel.apply {
            setOnClickListener {
                ic.deleteSurroundingText(1, 0)
            }
        }
        keyZ.setOnClickListener(getKeyClickListener())
        keyX.setOnClickListener(getKeyClickListener())
        keyC.setOnClickListener(getKeyClickListener())
        keyV.setOnClickListener(getKeyClickListener())
        keyB.setOnClickListener(getKeyClickListener())
        keyN.setOnClickListener(getKeyClickListener())
        keyM.setOnClickListener(getKeyClickListener())

        // handles only ACTION_DONE for now
        keyAc.apply {
            setOnClickListener {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
        }

        keyShift.apply {
            setOnClickListener {
                allCaps = !allCaps
                this@CustomKeyboard2.setAllCaps(allCaps)
            }
        }

        keyQuestion.setOnClickListener(getKeyClickListener())
        keyPeriod.setOnClickListener(getKeyClickListener())
        keyComma.setOnClickListener(getKeyClickListener())
        keyHyphen.setOnClickListener(getKeyClickListener())
        keySpace.setOnClickListener(getKeyClickListener(" "))
        keySm.setOnClickListener(getKeyClickListener())
        keyLa.setOnClickListener(getKeyClickListener())
        keyIc.setOnClickListener(getKeyClickListener())
    }

    private fun setAllCaps(allCaps: Boolean) {
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