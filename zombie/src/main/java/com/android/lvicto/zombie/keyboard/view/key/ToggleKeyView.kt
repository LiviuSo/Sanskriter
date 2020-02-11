package com.android.lvicto.zombie.keyboard.view.key

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.view.key.CandidatesKeyView

open class ToggleKeyView(context: Context, attrs: AttributeSet) : CandidatesKeyView(context, attrs) {

    var isPersistent = false
    private var mBackgroundToggledPersist: Int

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleKeyView)
        mBackgroundToggledPersist = typedArray.getResourceId(R.styleable.ToggleKeyView_backgroundToggled, R.color.colorAccent)
        typedArray.recycle()
    }

    fun setTogglePersistent(t: Boolean) {
        isPersistent = t
        if(t) {
            setToggledPersistentUI()
        }
    }

    fun setToggledPersistentUI() {
        background = ContextCompat.getDrawable(context, mBackgroundToggledPersist)
    }

}