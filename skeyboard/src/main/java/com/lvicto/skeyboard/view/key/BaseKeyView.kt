package com.lvicto.skeyboard.view.key

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.skeyboard.R

open class BaseKeyView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    private var mBackgroundNormal: Int
    private var mBackgroundPressed: Int
    private var mIsPressed = false
    private var mIcon: Int
    var mCode: Int = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseKeyView)
        mBackgroundNormal = typedArray.getResourceId(R.styleable.BaseKeyView_backgroundNormal, R.color.colorPrimary)
        mBackgroundPressed = typedArray.getResourceId(R.styleable.BaseKeyView_backgroundPressed, R.color.colorPrimaryDark)
        mIcon = typedArray.getResourceId(R.styleable.BaseKeyView_icon, 0)
        typedArray.getResourceId(R.styleable.BaseKeyView_code, 0).let {
            if(it != 0) {
                mCode = context.resources.getInteger(it)
            }
        }
        typedArray.recycle()

        isClickable = true
        setBackgroundNormal()
    }

    private fun setBackgroundNormal() {
        background = ContextCompat.getDrawable(context, mBackgroundNormal)
        mIsPressed = false
    }

    private fun setBackgroundPressed() {
        background = ContextCompat.getDrawable(context, mBackgroundPressed)
        mIsPressed = true
    }

    fun setPressedUI(pressed: Boolean) {
        if(pressed) {
            setBackgroundPressed()
        } else {
            setBackgroundNormal()
        }
    }

    fun isPressedUI(): Boolean {
        return mIsPressed
    }

    fun show(b: Boolean) {
        post {
            visibility = if (!b) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }
}