package com.android.lvicto.common.base

import android.text.Editable
import android.text.TextWatcher


abstract class BaseTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // nothing
    }

    override fun afterTextChanged(s: Editable?) {
        // nothing
    }
}