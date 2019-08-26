package com.android.lvicto.sanskritkeyboard.keyboard

import android.content.Context
import android.view.View
import android.widget.Button
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.utils.getVal
import com.android.lvicto.sanskritkeyboard.utils.layoutInflater

class KbLayoutInitializerPhoneLandscapeSa(context: Context) : KbLayoutInitializerPhonePortraitSa(context) {

    override fun initExtraCodes() {
        super.initExtraCodes()
        extraKeysCodesMap[R.integer.key_code_symbols.getVal(context)] = arrayListOf(
                R.integer.key_code_sa_stop.getVal(context),
                R.integer.key_code_sa_double_stop.getVal(context),
                R.integer.key_code_extra_apostrophy.getVal(context),
                R.integer.key_code_extra_accent1.getVal(context),
                R.integer.key_code_extra_accent2.getVal(context)
        )
        extraKeysCodesMap[R.integer.key_code_digits.getVal(context)] = arrayListOf(
                R.integer.key_code_sa_1.getVal(context),
                R.integer.key_code_sa_2.getVal(context),
                R.integer.key_code_sa_3.getVal(context),
                R.integer.key_code_sa_4.getVal(context),
                R.integer.key_code_sa_5.getVal(context),
                R.integer.key_code_sa_6.getVal(context),
                R.integer.key_code_sa_7.getVal(context),
                R.integer.key_code_sa_8.getVal(context),
                R.integer.key_code_sa_9.getVal(context),
                R.integer.key_code_sa_0.getVal(context)
        )
    }

    override fun getView(): View {
        return context.layoutInflater().inflate(R.layout.keyboard_phone_landscape_sa, null)
    }

    override fun bindKeys(view: View, showSymbolsOrDigits: Boolean) {
        super.bindKeys(view, false)
        view.findViewById<Button>(R.id.keyDigits).apply {
            setOnTouchListener(getSymbTouchListener(R.integer.key_code_symbols.getVal(context)))
        }
        view.findViewById<Button>(R.id.keySm).apply {
            setOnTouchListener(getSymbTouchListener(R.integer.key_code_symbols.getVal(context)))
        }
    }
}