package com.android.lvicto.sanskritkeyboard.keyboard

import android.content.Context
import android.view.View
import android.widget.Button
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.utils.getVal
import com.android.lvicto.sanskritkeyboard.utils.layoutInflater

class KbLayoutInitializerPhoneLandscapeQwerty(context: Context) : KbLayoutInitializerPhonePortraitQwerty(context) {

    override fun initExtraCodes() {
        super.initExtraCodes()
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

    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_phone_landscape_qwerty, null)

    override fun bindKeys(view: View, showSymbolsOrDigits: Boolean) {
        super.bindKeys(view, false)
        // just add the digits key touch listener
        view.findViewById<Button>(R.id.keyDigits).apply {
            setOnTouchListener(getSymbTouchListener(R.integer.key_code_digits.getVal(context)))
        }
    }
}