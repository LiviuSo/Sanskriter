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
import android.widget.Toast

class CustomKeyboard2 : InputMethodService() {

    private lateinit var ic: InputConnection
    private var config: Int? = null
    private var allCaps: Boolean = false
    private var allCapsPersist: Boolean = false
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
    private lateinit var keySettings: Button
    private lateinit var keyWord1: Button
    private lateinit var keyWord2: Button
    private lateinit var keyWord3: Button

    private lateinit var keyExtra1: Button
    private lateinit var keyExtra2: Button
    private lateinit var keyExtra3: Button
    private lateinit var keyExtra4: Button
    private lateinit var keyExtra5: Button
    private lateinit var keyExtra6: Button
    private lateinit var keyExtra7: Button
    private lateinit var keyExtra8: Button
    private lateinit var keyExtra9: Button
    private lateinit var keyExtra10: Button
    private lateinit var keyExtra11: Button
    private lateinit var keyExtra12: Button
    private lateinit var keyExtra13: Button
    private lateinit var keyExtra14: Button
    private val extraKeys: ArrayList<Button> = arrayListOf()
    private var extraKeysCodesMap = hashMapOf<Int, List<Int>>()


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
//                    view = layoutInflater.inflate(R.layout.keyboard_custom_layout_landscape, null)
                    view = layoutInflater.inflate(R.layout.keyboard_custom_layout_portrait, null)

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

    private fun getKeyClickListener(extra: Boolean = false, text: String = "") = View.OnClickListener {
        val key = it as Button
        val output = if (text.isEmpty()) {
            key.text
        } else {
            text
        }
        ic.commitText(output, output.length)
        disableAllExtraKeys()

        if (!extra) { // if not an alternate, show alternative keys
            if (output.length == 1) { // todo use tag (spike)
                showExtraKeys(output[0].toInt())
            }
        }

        if(allCaps && !allCapsPersist) { // todo add isLetter() condition
            toggleAllCaps()
        }
        Log.d(LOG_TAG, "key = ${output[0].toInt()}")
    }


    private fun getOnLongClickListener(extra: Boolean = false, text: String = "") = View.OnLongClickListener {
        val key = it as Button
        val output = if (text.isEmpty()) {
            key.text
        } else {
            text
        }
        if (!extra) { // if not extra, show alternative keys
            if (output.length == 1) { // todo use tag (spike)
                showExtraKeys(output[0].toInt())
            }
        }
        true
    }


    private fun disableAllExtraKeys() {
        extraKeys.forEach {
            it.text = applicationContext.resources.getString(R.string.key_placeholder)
            it.isEnabled = false
        }
    }

    private val symKeyClickListener = View.OnClickListener {
        Toast.makeText(applicationContext, "Symbol", Toast.LENGTH_SHORT).show()
    }

    private val lanKeyClickListener = View.OnClickListener {
        Toast.makeText(applicationContext, "Lan", Toast.LENGTH_SHORT).show()
    }

    private val settingsKeyClickListener = View.OnClickListener {
        Toast.makeText(applicationContext, "Settings", Toast.LENGTH_SHORT).show()
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
        keyPeriod = view.findViewById(R.id.keyPeriod)
        keyComma = view.findViewById(R.id.keyComma)
        keyHyphen = view.findViewById(R.id.keyHyphen)
        keySpace = view.findViewById(R.id.keySpace)
        keySm = view.findViewById(R.id.keySym)
        keyLa = view.findViewById(R.id.keyLang)
        keySettings = view.findViewById(R.id.keySettings)
        keyWord1 = view.findViewById(R.id.keySuggestion1)
        keyWord2 = view.findViewById(R.id.keySuggestion2)
        keyWord3 = view.findViewById(R.id.keySuggestion3)

        keyExtra1 = view.findViewById(R.id.keyLetterExtra1)
        keyExtra2 = view.findViewById(R.id.keyLetterExtra2)
        keyExtra3 = view.findViewById(R.id.keyLetterExtra3)
        keyExtra4 = view.findViewById(R.id.keyLetterExtra4)
        keyExtra5 = view.findViewById(R.id.keyLetterExtra5)
        keyExtra6 = view.findViewById(R.id.keyLetterExtra6)
        keyExtra7 = view.findViewById(R.id.keyLetterExtra7)
        keyExtra8 = view.findViewById(R.id.keyLetterExtra8)
        keyExtra9 = view.findViewById(R.id.keyLetterExtra9)
        keyExtra10 = view.findViewById(R.id.keyLetterExtra9)
        keyExtra11 = view.findViewById(R.id.keyLetterExtra9)
        keyExtra12 = view.findViewById(R.id.keyLetterExtra9)
        keyExtra13 = view.findViewById(R.id.keyLetterExtra9)
        keyExtra14 = view.findViewById(R.id.keyLetterExtra9)

        initExtraCodes()
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
                if(!allCaps) {
                    toggleAllCaps()
                } else if(allCaps && !allCapsPersist) {
                    toggleAllCaps()
                } else {
                    toggleAllCaps()
                    allCapsPersist = false
                }
            }

            setOnLongClickListener {
                if(!allCaps) {
                    toggleAllCaps()
                    allCapsPersist = true
                }
                true
            }
        }

        keyQuestion.setOnClickListener(getKeyClickListener())
        keyPeriod.setOnClickListener(getKeyClickListener())
        keyComma.setOnClickListener(getKeyClickListener())
        keyHyphen.setOnClickListener(getKeyClickListener())
        keySpace.setOnClickListener(getKeyClickListener(false, " "))
        keySm.setOnClickListener(symKeyClickListener)
        keyLa.setOnClickListener(lanKeyClickListener)

        keySettings.setOnClickListener(settingsKeyClickListener)
        keyWord1.setOnClickListener(getKeyClickListener())
        keyWord2.setOnClickListener(getKeyClickListener())
        keyWord3.setOnClickListener(getKeyClickListener())

        keyExtra1.setOnClickListener(getKeyClickListener())
        keyExtra2.setOnClickListener(getKeyClickListener())
        keyExtra3.setOnClickListener(getKeyClickListener())
        keyExtra4.setOnClickListener(getKeyClickListener())
        keyExtra5.setOnClickListener(getKeyClickListener())
        keyExtra6.setOnClickListener(getKeyClickListener())
        keyExtra7.setOnClickListener(getKeyClickListener())
        keyExtra8.setOnClickListener(getKeyClickListener())
        keyExtra9.setOnClickListener(getKeyClickListener())

        // long click listeners
        keyA.setOnLongClickListener(getOnLongClickListener())

        initExtraKeys()
    }

    private fun toggleAllCaps() {
        allCaps = !allCaps
        this@CustomKeyboard2.setAllCaps(allCaps)
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

        extraKeys.forEach {
            if(it.isEnabled) {
                it.text = setCase(it, allCaps)
            }
        }
    }

    private fun setCase(button: Button, allCaps: Boolean): String = if (allCaps) {
        button.text.toString().toUpperCase()
    } else {
        button.text.toString().toLowerCase()
    }

    private fun initExtraKeys() {
        keyExtra1.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra2.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra3.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra4.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra5.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra6.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra7.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra8.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra9.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra10.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra11.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra12.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra13.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
        keyExtra14.apply {
            extraKeys.add(this)
            this.isEnabled = false
            setOnClickListener(getKeyClickListener(true))
        }
    }

    private fun showExtraKeys(code: Int) {
        val relatedChars = getRelatedCharsRes(code)
        if (relatedChars != null) {
            (0 until relatedChars.size).forEach { index ->
                extraKeys[index].text = "${relatedChars[index].toChar()}"
                extraKeys[index].isEnabled = true
            }
        }
    }

    private fun getRelatedCharsRes(code: Int): ArrayList<Int>? {
        return if (extraKeysCodesMap.containsKey(code)) {
            extraKeysCodesMap[code] as ArrayList<Int>
        } else {
            null
        }
    }

    private fun initExtraCodes() {
        val res = applicationContext.resources
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_a)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_a_ro1),
                res.getInteger(R.integer.key_code_letter_a_ro2),
                res.getInteger(R.integer.key_code_letter_a_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_A)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_A_ro1),
                res.getInteger(R.integer.key_code_letter_A_ro2),
                res.getInteger(R.integer.key_code_letter_A_sa)
        )
        // todo complete
    }

    companion object {
        const val LOG_TAG = "CustomKeyboard2"
    }
}