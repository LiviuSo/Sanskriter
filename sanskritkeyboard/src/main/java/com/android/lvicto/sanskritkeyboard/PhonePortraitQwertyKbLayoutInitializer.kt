package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.Toast

class PhonePortraitQwertyKbLayoutInitializer(context: Context) : KeyboardLayoutInitializer(context) {

    private var allCaps: Boolean = false
    private var allCapsPersist: Boolean = false
    private var keysToAllCaps = arrayListOf<Button>()

    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_qwerty_phone_portrait, null)

    override fun bindKeys(view: View) {
        (view.findViewById(R.id.keyDigit1) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit2) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit3) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit4) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit5) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit6) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit7) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit8) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit9) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyDigit0) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }

        (view.findViewById(R.id.keyQ) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyW) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyE) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyR) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyT) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyY) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyU) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyI) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyO) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyP) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyA) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyS) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyD) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyF) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyG) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyH) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyJ) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyK) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyL) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyZ) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyX) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyV) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyB) as Button).apply {
            setOnClickListener(getKeyClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyN) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }
        (view.findViewById(R.id.keyM) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
            keysToAllCaps.add(this)
        }

        (view.findViewById(R.id.keyDel) as Button).apply {
            setOnClickListener {
                ic.deleteSurroundingText(1, 0)
            }
        }
        (view.findViewById(R.id.keyAction) as Button).apply {
            setOnClickListener {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
        }
        (view.findViewById(R.id.keyShift) as Button).apply {
            setOnClickListener {
                if (!allCaps) {
                    toggleAllCaps()
                } else if (allCaps && !allCapsPersist) {
                    toggleAllCaps()
                } else {
                    toggleAllCaps()
                    allCapsPersist = false
                }
            }

            setOnLongClickListener {
                if (!allCaps) {
                    toggleAllCaps()
                    allCapsPersist = true
                }
                true
            }
        }
        (view.findViewById(R.id.keyPeriod) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
        }
        (view.findViewById(R.id.keyComma) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyQuestion) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keySpace) as Button).apply {
            setOnClickListener(getKeyClickListener(false, " "))
            setOnLongClickListener {
                Toast.makeText(context, "Lan", Toast.LENGTH_SHORT).show()
                true
            }
        }
        (view.findViewById(R.id.keyExclamation) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyHyphen) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
        }
        (view.findViewById(R.id.keyAt) as Button).apply {
            setOnClickListener(getKeyClickListener())
            setOnLongClickListener(getKeyLongClickListener())
        }
        (view.findViewById(R.id.keySettings) as Button).apply {
            setOnClickListener(settingsKeyClickListener)
        }
        (view.findViewById(R.id.keySuggestion1) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keySuggestion2) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keySuggestion3) as Button).apply {
            setOnClickListener(getKeyClickListener())
        }

        (view.findViewById(R.id.keyLetterExtra1) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra2) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra3) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra4) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra5) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra6) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra7) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra8) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra9) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra10) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra11) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra12) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra13) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
        (view.findViewById(R.id.keyLetterExtra14) as Button).apply {
            extraKeys.add(this)
            isEnabled = false
            setOnClickListener(getKeyClickListener())
        }
    }

    override fun initExtraCodes() {
        val res = context.resources
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_a)] = arrayListOf(
                R.integer.key_code_letter_a_ro1.getVal(context),
                res.getInteger(R.integer.key_code_letter_a_ro2),
                res.getInteger(R.integer.key_code_letter_a_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_A)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_A_ro1),
                res.getInteger(R.integer.key_code_letter_A_ro2),
                res.getInteger(R.integer.key_code_letter_A_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_i)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_i_ro),
                res.getInteger(R.integer.key_code_letter_i_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_I)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_I_ro),
                res.getInteger(R.integer.key_code_letter_I_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_l)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_l_sa),
                res.getInteger(R.integer.key_code_letter_l_sa_long)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_L)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_L_sa),
                res.getInteger(R.integer.key_code_letter_L_sa_long)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_m)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_m_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_M)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_M_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_n)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_n_sa1),
                res.getInteger(R.integer.key_code_letter_n_sa2),
                res.getInteger(R.integer.key_code_letter_n_sa3)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_N)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_N_sa1),
                res.getInteger(R.integer.key_code_letter_N_sa2),
                res.getInteger(R.integer.key_code_letter_N_sa3)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_r)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_r_sa1),
                res.getInteger(R.integer.key_code_letter_r_sa2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_R)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_R_sa1),
                res.getInteger(R.integer.key_code_letter_R_sa2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_s)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_s_ro),
                res.getInteger(R.integer.key_code_letter_s_sa1),
                res.getInteger(R.integer.key_code_letter_s_sa2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_S)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_S_ro),
                res.getInteger(R.integer.key_code_letter_S_sa1),
                res.getInteger(R.integer.key_code_letter_S_sa2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_t)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_t_ro),
                res.getInteger(R.integer.key_code_letter_t_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_T)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_T_ro),
                res.getInteger(R.integer.key_code_letter_T_sa)
        )
        extraKeysCodesMap[R.integer.key_code_letter_d.getVal(context)] = arrayListOf(
                R.integer.key_code_letter_d_sa.getVal(context)
        )
        extraKeysCodesMap[R.integer.key_code_letter_D.getVal(context)] = arrayListOf(
                R.integer.key_code_letter_D_sa.getVal(context)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_u)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_u_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_U)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_U_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_h)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_h_sa)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_H)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_H_sa)
        )
        extraKeysCodesMap[R.integer.key_code_period.getVal(context)] = arrayListOf(
                R.integer.key_code_apostrophy.getVal(context),
                R.integer.key_code_quote.getVal(context),
                R.integer.key_code_semicolon.getVal(context),
                R.integer.key_code_colon.getVal(context)
        )

        extraKeysCodesMap[R.integer.key_code_hyphen.getVal(context)] = arrayListOf(
                R.integer.key_code_plus.getVal(context),
                R.integer.key_code_multiplication.getVal(context),
                R.integer.key_code_division.getVal(context),
                R.integer.key_code_lt.getVal(context),
                R.integer.key_code_gt.getVal(context),
                R.integer.key_code_equal.getVal(context),
                R.integer.key_code_xor.getVal(context),
                R.integer.key_code_percent.getVal(context),
                R.integer.key_code_tilda.getVal(context)
        )

        extraKeysCodesMap[R.integer.key_code_at.getVal(context)] = arrayListOf(
                R.integer.key_code_underscore.getVal(context),
                R.integer.key_code_slash.getVal(context),
                R.integer.key_code_pipe.getVal(context),
                R.integer.key_code_cent.getVal(context),
                R.integer.key_code_back_apostrophe.getVal(context),
                R.integer.key_code_dollar.getVal(context),
                R.integer.key_code_pound.getVal(context),
                R.integer.key_code_backslash.getVal(context),

                R.integer.key_code_open_paranthesis.getVal(context),
                R.integer.key_code_close_paranthesis.getVal(context),
                R.integer.key_code_open_square_paranthesis.getVal(context),
                R.integer.key_code_close_square_paranthesis.getVal(context),
                R.integer.key_code_open_curly_brace.getVal(context),
                R.integer.key_code_close_curly_brace.getVal(context)
        )
    }

    override fun getKeyClickListener(extra: Boolean, text: String) = View.OnClickListener {
        val key = it as Button
        val output = if (text.isEmpty()) {
            key.text
        } else {
            text
        }
        // todo logic for label with multiple characters
        val success = ic.commitText(output, output.length)
        disableAllExtraKeys()

        if (!extra) { // if not an alternate, show alternative keys
            showExtraKeys(output[0].toInt())
        }

        if (allCaps && !allCapsPersist) { // todo add isLetter() condition
            toggleAllCaps()
        }

        Log.d(CustomKeyboard2.LOG_TAG, "key = ${output[0].toInt()} committed: $success ic: $ic") // debug
    }

    override fun getKeyLongClickListener(extra: Boolean, text: String) = View.OnLongClickListener {
        disableAllExtraKeys()
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

    private fun toggleAllCaps() {
        allCaps = !allCaps
        setAllCaps(allCaps)
    }

    private fun setCase(button: Button, allCaps: Boolean): String = if (allCaps) {
        button.text.toString().toUpperCase()
    } else {
        button.text.toString().toLowerCase()
    }

    private fun setAllCaps(allCaps: Boolean) {
        keysToAllCaps.forEach {
            setCase(it, allCaps)
        }

        extraKeys.forEach {
            if (it.isEnabled) {
                it.text = setCase(it, allCaps)
            }
        }
    }
}
