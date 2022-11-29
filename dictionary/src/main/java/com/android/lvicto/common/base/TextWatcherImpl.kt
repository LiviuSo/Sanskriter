package com.android.lvicto.common.base

import android.text.Editable
import android.text.TextWatcher


open class TextWatcherImpl : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // nothing
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // nothing
    }

    override fun afterTextChanged(s: Editable?) {
        // nothing
    }
}