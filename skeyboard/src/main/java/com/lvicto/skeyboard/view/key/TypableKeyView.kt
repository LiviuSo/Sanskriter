package com.lvicto.skeyboard.view.key

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.IntegerRes
import com.example.skeyboard.R

open class TypableKeyView(context: Context, attr: AttributeSet ) : CandidatesKeyView(context, attr) {

    private lateinit var popup: PopupWindow

    var label: String? = ""
    @IntegerRes var labelResId: Int = -1

    init {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.TypableKeyView)
        label = typedArray.getString(R.styleable.TypableKeyView_label)
        labelResId = typedArray.getResourceId(R.styleable.TypableKeyView_label, -1)
        typedArray.recycle()
    }

    /**
     * Show key preview
     */
    fun showPreview() {
        val rect = this.locateView()
        popup = this.createPopup(getKeyLabel())
        popup.show(this, rect)
    }

    /**
     * Hide key preview
     */
    fun hidePreview() {
        popup.dismiss() // might not work
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
        popup.contentView = LayoutInflater.from(context).inflate(R.layout.popup_key_preview, null)
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
}