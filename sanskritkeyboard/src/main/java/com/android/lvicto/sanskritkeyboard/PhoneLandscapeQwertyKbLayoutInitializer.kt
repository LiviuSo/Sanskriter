package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.view.View

class PhoneLandscapeQwertyKbLayoutInitializer(context: Context) : KeyboardLayoutInitializer(context) {
    override fun initExtraCodes() {

    }

    override fun getView(): View {
        return View(context)
    }

    override fun bindKeys(view: View) {
    }

    override fun getKeyClickListener(extra: Boolean, text: String): View.OnClickListener = View.OnClickListener {
    }

    override fun getKeyLongClickListener(extra: Boolean, text: String): View.OnLongClickListener = View.OnLongClickListener {
        true
    }
}
