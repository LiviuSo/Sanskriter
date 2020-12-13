package com.lvicto.skeyboard.view.key

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import java.util.*

open class QwertyShiftKeyView(context: Context, attr: AttributeSet)
    : TypableKeyView(context, attr), ShiftKey {

    private var isUpperCase = false

    override fun shiftKey(shifted: Boolean) {
        this.let {
            isUpperCase = shifted
            val key = it as TextView
            it.post {
                key.text = if(shifted) {
                    key.text.toString().toUpperCase(Locale.getDefault())
                } else {
                    key.text.toString().toLowerCase(Locale.getDefault())
                }
            }
        }
    }
}