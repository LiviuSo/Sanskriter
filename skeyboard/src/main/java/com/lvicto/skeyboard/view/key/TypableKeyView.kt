package com.lvicto.skeyboard.view.key

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.IntegerRes
import com.example.skeyboard.R

open class TypableKeyView(context: Context, attr: AttributeSet ) : CandidatesKeyView(context, attr) {

    var label: String? = ""
    @IntegerRes var labelResId: Int = -1

    init {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.TypableKeyView)
        label = typedArray.getString(R.styleable.TypableKeyView_label)
        labelResId = typedArray.getResourceId(R.styleable.TypableKeyView_label, -1)
        typedArray.recycle()
    }

    fun showPreview() {
        // todo
    }

    fun hidePreview() {
        // todo
    }

    fun getKeyLabel(): String {
        return label as String
    }

    fun resetText() {
        post {
            text = label
        }
    }

    fun setText(s: String) {
        post {
            text = s
        }
    }

    fun setPlaceholder() {
        setText("*") // todo crete ExtraKeyView and add placehoder stylable
    }

    fun enable(b: Boolean) {
        post {
           isEnabled = b
        }
    }
}