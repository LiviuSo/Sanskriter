package com.android.lvicto.sanskritkeyboard.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.IdRes
import com.android.lvicto.sanskritkeyboard.R


fun Context.getOrientation(): Int = this.resources?.configuration?.orientation!!

fun Context.isTablet(): Boolean {
    val xlarge = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == 4
    val large = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
    return xlarge || large
}

infix fun Int.getVal(context: Context): Int = context.resources.getInteger(this)

fun Context.layoutInflater(): LayoutInflater =
        this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

infix fun View.button(@IdRes id: Int): Button = (this.findViewById(id) as Button)

fun View.locateView(): Rect {
    val locInt = IntArray(2)
    try {
        this.getLocationInWindow(locInt)
    } catch (npe: Throwable) {
        // Happens when the view doesn't exist on screen anymore.
        return Rect(-1, -1, -1, -1)
    }

    val location = Rect()
    location.left = locInt[0]
    location.top = locInt[1]
    location.right = location.left + this.width
    location.bottom = location.top + this.height
    return location
}

fun View.createPopup(text: String): PopupWindow {
    val context = this.context
    val popup = PopupWindow(this)
    popup.contentView = context.layoutInflater().inflate(R.layout.popup_key_preview, null)
    popup.isOutsideTouchable = true
    popup.contentView.findViewById<TextView>(R.id.tvPreview).text = text
    return popup
}

fun PopupWindow.show(parent: View, rect: Rect) {
    val height = rect.bottom - rect.top
    val width = rect.right - rect.left
    val previewVertDist = parent.context.resources.getDimension(R.dimen.key_margin).toInt() // use the distance between the rows

    this.showAtLocation(parent.rootView, Gravity.START or Gravity.TOP,
            rect.left - width / 4,
            rect.top - height - height / 2 - previewVertDist)
    this.update(width + width / 2, height + height / 2)
}


infix fun View.frameLayout(id: Int): FrameLayout {
    return this.findViewById(id) as FrameLayout
}

