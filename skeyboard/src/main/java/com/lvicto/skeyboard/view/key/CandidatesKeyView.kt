package com.lvicto.skeyboard.view.key

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ArrayRes
import com.example.skeyboard.R

open class CandidatesKeyView(context: Context, attrs: AttributeSet) : BaseKeyView(context, attrs) {

    @ArrayRes var candidatesResId: Int
    var candidatesLabels: Array<String> = arrayOf()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CandidatesKeyView)
        candidatesResId = typedArray.getResourceId(R.styleable.CandidatesKeyView_candidates, 0)
        if (candidatesResId != 0) {
            candidatesLabels = context.resources.getStringArray(candidatesResId)
        }
        typedArray.recycle()
    }

    fun getCandidatesLabels(): List<String> {
        return candidatesLabels.toList()
    }


}