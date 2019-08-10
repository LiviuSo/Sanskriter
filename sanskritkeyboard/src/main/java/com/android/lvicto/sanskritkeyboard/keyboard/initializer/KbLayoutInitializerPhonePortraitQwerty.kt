package com.android.lvicto.sanskritkeyboard.keyboard.initializer

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import com.android.lvicto.sanskritkeyboard.CustomKeyboard2.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.button
import com.android.lvicto.sanskritkeyboard.getVal
import com.android.lvicto.sanskritkeyboard.layoutInflater

open class KbLayoutInitializerPhonePortraitQwerty(context: Context) :
        KbLayoutInitializer(context) {
    private var allCaps: Boolean = false
    private var allCapsPersist: Boolean = false
    private var keysToAllCaps = arrayListOf<Button>()

    // todo convert to a touch listener
    private val shiftOnClickListener: View.OnClickListener = View.OnClickListener {
        Log.d(LOG_TAG, "shiftOnClickListener: $allCaps")
        if (!allCaps) {
            toggleAllCaps()
        } else if (allCaps && !allCapsPersist) {
            toggleAllCaps()
        } else {
            toggleAllCaps()
            allCapsPersist = false
        }
    }

    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_phone_portrait_qwerty, null)

    override fun bindKeys(view: View) {
        super.bindKeys(view)
        val keysClickListenerOnly = arrayListOf(
                view button R.id.keyDigit1
                , view button R.id.keyDigit2
                , view button R.id.keyDigit3
                , view button R.id.keyDigit4
                , view button R.id.keyDigit5
                , view button R.id.keyDigit6
                , view button R.id.keyDigit7
                , view button R.id.keyDigit8
                , view button R.id.keyDigit9
                , view button R.id.keyDigit0
                , view button R.id.keyComma
                , view button R.id.keyQuestion
                , view button R.id.keyExclamation
                , view button R.id.keyPeriod
                , view button R.id.keyHyphen
                , view button R.id.keyAt
        )
        keysClickListenerOnly.forEach {
            it.setOnTouchListener(getCommonTouchListener())
        }

        val keysClickListenerOnlyAllCpas = arrayListOf(
                view button R.id.keyQ
                , view button R.id.keyW
                , view button R.id.keyE
                , view button R.id.keyY
                , view button R.id.keyO
                , view button R.id.keyP
                , view button R.id.keyF
                , view button R.id.keyZ
                , view button R.id.keyX
                , view button R.id.keyV
                , view button R.id.keyB
                , view button R.id.keyJ
                , view button R.id.keyK
                , view button R.id.keyG
                , view button R.id.keyR
                , view button R.id.keyT
                , view button R.id.keyU
                , view button R.id.keyI
                , view button R.id.keyA
                , view button R.id.keyS
                , view button R.id.keyD
                , view button R.id.keyH
                , view button R.id.keyL
                , view button R.id.keyN
                , view button R.id.keyM
                , view button R.id.keyC
        )
        keysClickListenerOnlyAllCpas.forEach {
            it.setOnTouchListener(getCommonTouchListener())
            keysToAllCaps.add(it)
        }

        (view button R.id.keyShift).apply {
            setOnClickListener(shiftOnClickListener)

            setOnLongClickListener {
                if (!allCaps) {
                    toggleAllCaps()
                    allCapsPersist = true
                }
                true
            }
        }

        initExtraKeys(view)
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

    override fun toggleShiftBack() {
        if(allCaps && !allCapsPersist) {
            toggleAllCaps()
        }
    }

    private fun toggleAllCaps() {
        allCaps = !allCaps
        setAllCaps(allCaps)
    }

    private fun setCase(button: Button, allCaps: Boolean): String {
        val txt = button.text.toString()
        val newTxt = if (allCaps) {
            txt.toUpperCase()
        } else {
            txt.toLowerCase()
        }
        button.text = newTxt
        return newTxt
    }

    private fun setAllCaps(allCaps: Boolean) {
        keysToAllCaps.forEach {
            setCase(it, allCaps)
        }

        extraKeys.forEach {
            if (it.isEnabled) {
                setCase(it, allCaps)
            }
        }
    }
}
