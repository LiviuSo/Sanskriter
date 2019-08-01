package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.view.View

class PhonePortraitSaKbLayoutInitializer(context: Context) : KeyboardLayoutInitializer(context) {

    override fun initExtraCodes() {

    }

    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_sa_phone_portrait, null)

    override fun bindKeys(view: View) {
    }

    override fun getKeyClickListener(extra: Boolean, text: String): View.OnClickListener = View.OnClickListener {
    }

    override fun getKeyLongClickListener(extra: Boolean, text: String): View.OnLongClickListener = View.OnLongClickListener {
        true
    }

}
