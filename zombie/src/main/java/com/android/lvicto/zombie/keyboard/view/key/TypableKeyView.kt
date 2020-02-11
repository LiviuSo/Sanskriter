package com.android.lvicto.zombie.keyboard.view.key

import android.content.Context
import android.util.AttributeSet
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.view.key.CandidatesKeyView

open class TypableKeyView(context: Context, attr: AttributeSet ) : CandidatesKeyView(context, attr) {

    var mLabel: String? = ""

    init {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.TypableKeyView)
        mLabel = typedArray.getString(R.styleable.TypableKeyView_label)
        typedArray.recycle()
    }

    fun showPreview() {
        // todo
    }

    fun hidePreview() {
        // todo
    }

    fun getKeyLabel(): String {
        return mLabel as String
    }

    fun resetText() {
        post {
            text = mLabel
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