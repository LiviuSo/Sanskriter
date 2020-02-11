package com.android.lvicto.zombie.keyboard.view.key

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import java.util.*

class ShiftableKeyView(context: Context, attr: AttributeSet) : TypableKeyView(context, attr) {

    var isUpperCase = false

    fun setCaps(capsOn: Boolean) {
        this.let {
            isUpperCase = capsOn
            val key = it as TextView
            it.post {
                key.text = if(capsOn) {
                    key.text.toString().toUpperCase(Locale.getDefault())
                } else {
                    key.text.toString().toLowerCase(Locale.getDefault())
                }
            }
        }
    }

}