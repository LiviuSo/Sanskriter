package com.android.lvicto.sanskritkeyboard.keyboard

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.android.lvicto.sanskritkeyboard.service.SanskritCustomKeyboard.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.utils.button
import com.android.lvicto.sanskritkeyboard.utils.getVal
import com.android.lvicto.sanskritkeyboard.utils.layoutInflater
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

open class KbLayoutInitPhoneQwertyPortrait(context: Context) :
        KbLayoutInitializer(context) {
    private var allCaps: Boolean = false
    private var allCapsPersist: Boolean = false
    private var keysToAllCaps = arrayListOf<Button>()

    private val shiftTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        var actionTime = 0L
        lateinit var actionDownFlag: AtomicBoolean
        val runnable = Runnable {
            while (!actionDownFlag.get()) {
                Log.d(LOG_TAG, "shiftTouchListener: Touching Down")
                if (System.currentTimeMillis() - actionTime > LONG_PRESS_TIME) {
                    Log.d(LOG_TAG, "shiftTouchListener: Long tap")
                    allCapsPersist = if (!allCapsPersist) {
                        true // toggle shift permanently
                    } else {
                        toggleAllCaps() // reset
                        false
                    }
                    actionDownFlag.set(true) // once long tap reached, end the thread
                }
            }
            Log.d(LOG_TAG, "Not Touching")
        }

        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            return when (motionEvent?.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!allCaps) {
                        toggleAllCaps()
                    } else if (allCaps && !allCapsPersist) {
                        toggleAllCaps()
                    } else {
                        toggleAllCaps()
                        allCapsPersist = false
                    }
                    actionDownFlag = AtomicBoolean(false)
                    actionTime = System.currentTimeMillis()
                    Thread(runnable).start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    actionDownFlag.set(true)
                    view?.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_phone_qwerty_portrait, null)

    override fun bindKeys(view: View) {
        super.bindKeys(view)
        val keysClickListenerOnly = arrayListOf(
                view button R.id.keyApostrophe
                , view button R.id.keySep
        )
        keysClickListenerOnly.forEach {
            it.setOnTouchListener(getCommonTouchListener())
        }

        val keysClickListenerOnlyAllCaps = arrayListOf(
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
        keysClickListenerOnlyAllCaps.forEach {
            it.setOnTouchListener(getCommonTouchListener())
            keysToAllCaps.add(it)
        }

        (view button R.id.keyShift).apply {
            setOnTouchListener(shiftTouchListener)
        }

        view.findViewById<Button>(R.id.keySymbol).apply {
            setOnTouchListener(getSymbTouchListener(R.integer.key_code_symbols.getVal(context)))
        }

        initExtraKeys(view)
    }

    override fun initExtraCodes() {
        val res = context.resources
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_a)] = arrayListOf(
                R.integer.key_code_letter_a_ro1.getVal(context),
                res.getInteger(R.integer.key_code_letter_a_ro2),
                res.getInteger(R.integer.key_code_letter_a_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_A)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_A_ro1),
                res.getInteger(R.integer.key_code_letter_A_ro2),
                res.getInteger(R.integer.key_code_letter_A_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_i)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_i_ro),
                res.getInteger(R.integer.key_code_letter_i_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_I)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_I_ro),
                res.getInteger(R.integer.key_code_letter_I_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_l)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_l_sa),
                res.getInteger(R.integer.key_code_letter_l_sa_long),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_L)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_L_sa),
                res.getInteger(R.integer.key_code_letter_L_sa_long),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_m)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_m_sa),
                res.getInteger(R.integer.key_code_letter_m2_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_M)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_M_sa),
                res.getInteger(R.integer.key_code_letter_M2_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_n)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_n_sa1),
                res.getInteger(R.integer.key_code_letter_n_sa2),
                res.getInteger(R.integer.key_code_letter_n_sa3),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_N)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_N_sa1),
                res.getInteger(R.integer.key_code_letter_N_sa2),
                res.getInteger(R.integer.key_code_letter_N_sa3),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_r)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_r_sa1),
                res.getInteger(R.integer.key_code_letter_r_sa2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_R)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_R_sa1),
                res.getInteger(R.integer.key_code_letter_R_sa2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_s)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_s_ro),
                res.getInteger(R.integer.key_code_letter_s_sa1),
                res.getInteger(R.integer.key_code_letter_s_sa2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_S)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_S_ro),
                res.getInteger(R.integer.key_code_letter_S_sa1),
                res.getInteger(R.integer.key_code_letter_S_sa2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_t)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_t_ro),
                res.getInteger(R.integer.key_code_letter_t_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_T)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_T_ro),
                res.getInteger(R.integer.key_code_letter_T_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_d)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_d_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_D)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_D_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )

        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_u)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_u_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_U)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_U_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_h)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_h_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_H)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_H_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_e)] = arrayListOf(
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_y)] = arrayListOf(
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_o)] = arrayListOf(
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )

        extraKeysCodesMap[R.integer.key_code_apostrophy.getVal(context)] = arrayListOf(
                R.integer.key_code_period.getVal(context),
                R.integer.key_code_comma.getVal(context),
                R.integer.key_code_exclamation_mark.getVal(context),
                R.integer.key_code_question_mark.getVal(context),
                R.integer.key_code_quote.getVal(context),
                R.integer.key_code_semicolon.getVal(context),
                R.integer.key_code_colon.getVal(context),
                R.integer.key_code_at.getVal(context),
                R.integer.key_code_open_paranthesis.getVal(context),
                R.integer.key_code_close_paranthesis.getVal(context)
        )

        extraKeysCodesMap[R.integer.key_code_pipe.getVal(context)] = arrayListOf(
                R.integer.key_code_underscore.getVal(context),
                R.integer.key_code_cent.getVal(context),
                R.integer.key_code_dollar.getVal(context),
                R.integer.key_code_pound.getVal(context),
                R.integer.key_code_slash.getVal(context),
                R.integer.key_code_backslash.getVal(context),
                R.integer.key_code_open_square_paranthesis.getVal(context),
                R.integer.key_code_close_square_paranthesis.getVal(context),
                R.integer.key_code_open_curly_brace.getVal(context),
                R.integer.key_code_close_curly_brace.getVal(context)
        )

        extraKeysCodesMap[R.integer.key_code_symbols.getVal(context)] = arrayListOf(
                R.integer.key_code_hyphen.getVal(context),
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
        extraKeysCodesMap[R.integer.key_code_digits.getVal(context)] = arrayListOf(
                R.integer.key_code_digit_0.getVal(context),
                R.integer.key_code_digit_1.getVal(context),
                R.integer.key_code_digit_2.getVal(context),
                R.integer.key_code_digit_3.getVal(context),
                R.integer.key_code_digit_4.getVal(context),
                R.integer.key_code_digit_5.getVal(context),
                R.integer.key_code_digit_6.getVal(context),
                R.integer.key_code_digit_7.getVal(context),
                R.integer.key_code_digit_8.getVal(context),
                R.integer.key_code_digit_9.getVal(context)
        )
    }

    override fun toggleShiftBack() {
        if (allCaps && !allCapsPersist) {
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
            txt.toUpperCase(Locale.getDefault())
        } else {
            txt.toLowerCase(Locale.getDefault())
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
