package com.lvicto.skeyboard.view.key

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import java.util.*

class ExtraIastShiftKeyView(context: Context, attr: AttributeSet) : ExtraKeyView(context, attr), ShiftKey {

    var isUpperCase = false

    override fun shiftKey(shifted: Boolean) {
        this.let {
            isUpperCase = shifted
            val key = it as TextView
            it.post {
                key.text = if(shifted) {
                    key.text.toString().uppercase(Locale.getDefault())
                } else {
                    key.text.toString().lowercase(Locale.getDefault())
                }
            }
        }
    }
}