package com.android.lvicto.sanskritkeyboard.keyboard

import android.content.Context
import android.view.View
import android.widget.Button
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.utils.getVal
import com.android.lvicto.sanskritkeyboard.utils.layoutInflater

open class KbLayoutInitPhoneQwertyLandscape(context: Context) : KbLayoutInitPhoneQwertyPortrait(context) {

    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_phone_qwerty_landscape, null)

    override fun bindKeys(view: View) {
        super.bindKeys(view)
        // just add the digits key touch listener
        view.findViewById<Button>(R.id.keyDigits).apply {
            setOnTouchListener(getSymbTouchListener(R.integer.key_code_digits.getVal(context)))
        }
    }
}