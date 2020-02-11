package com.android.lvicto.zombie.keyboard.view.key

import android.content.Context
import android.util.AttributeSet
import com.android.lvicto.zombie.R

open class CandidatesKeyView(context: Context, attrs: AttributeSet) : BaseKeyView(context, attrs) {

    var mCandidatesLabels: Array<String> = arrayOf()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CandidatesKeyView)
        val mCandidatesResId = typedArray.getResourceId(R.styleable.CandidatesKeyView_candidates, 0)
        if (mCandidatesResId != 0) {
            mCandidatesLabels = context.resources.getStringArray(mCandidatesResId)
        }
        typedArray.recycle()
    }

    fun getCandidatesLabels(): List<String> {
        return mCandidatesLabels.toList()
    }


}