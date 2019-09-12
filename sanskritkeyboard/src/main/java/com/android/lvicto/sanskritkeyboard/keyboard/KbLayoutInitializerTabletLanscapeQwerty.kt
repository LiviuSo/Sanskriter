package com.android.lvicto.sanskritkeyboard.keyboard

import android.content.Context
import android.view.View
import android.widget.Button
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.utils.button
import com.android.lvicto.sanskritkeyboard.utils.getVal
import com.android.lvicto.sanskritkeyboard.utils.layoutInflater

class KbLayoutInitializerTabletLanscapeQwerty(context: Context) : KbLayoutInitializerTabletPortraitQwerty(context) {
    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_tablet_landscape_qwerty, null)

    override fun initExtraCodes() {
        val res = context.resources
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_a)] = arrayListOf(
                R.integer.key_code_letter_a_ro1.getVal(context),
                res.getInteger(R.integer.key_code_letter_a_ro2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_A)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_A_ro1),
                res.getInteger(R.integer.key_code_letter_A_ro2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_i)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_i_ro)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_I)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_I_ro)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_l_sa)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_l_sa_long)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_L_sa)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_L_sa_long)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_r_sa1)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_r_sa2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_R_sa1)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_R_sa2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_s)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_s_ro)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_S)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_S_ro)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_t)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_t_ro)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_T)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_T_ro)
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

    override fun bindKeys(view: View, showSymbolsOrDigits: Boolean) {
        super.bindKeys(view, true)
        val sanskritKeys = arrayListOf(
                view button R.id.keySaALong
                , view button R.id.keySaILong
                , view button R.id.keySaULong
                , view button R.id.keySaR
                , view button R.id.keySaL
                , view button R.id.keySaT
                , view button R.id.keySaD
                , view button R.id.keySaS1
                , view button R.id.keySaS2
                , view button R.id.keySaN1
                , view button R.id.keySaN2
        )
        sanskritKeys.forEach {
            it.setOnTouchListener(getCommonTouchListener())
        }
    }
}