package com.android.lvicto.sanskritkeyboard.newkeyboard.component

import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.newkeyboard.SanskritKeyboard
import com.android.lvicto.sanskritkeyboard.utils.createPopup
import com.android.lvicto.sanskritkeyboard.utils.locateView
import com.android.lvicto.sanskritkeyboard.utils.show

class KeyboardKey(val code: Int,
                  private val candidates: List<Int>?,
                  @DrawableRes val normalDrawable: Int = R.drawable.key_normal,
                  @DrawableRes val pressedDrawable: Int = R.drawable.key_pressed,
                  @DrawableRes val toggleDrawable: Int = 0) {

    lateinit var keyboard: SanskritKeyboard // todo inject
    lateinit var view: View
    private var extrasToggledOn: Boolean = false
    private lateinit var popup: PopupWindow

    fun toggleOn(on: Boolean) {
        extrasToggledOn = on
        if(on) {
            setToggled(view)
        } else {
            setPressed(false)
        }
    }

    fun setPressed(pressed: Boolean) {
        val drawable = if(pressed) {
            pressedDrawable
        } else {
            normalDrawable
        }
        view.background = ContextCompat.getDrawable(keyboard.context, drawable)
    }

    private fun setToggled(view: View) {
        view.background = ContextCompat.getDrawable(keyboard.context, toggleDrawable)
    }

    fun getCandidates(): List<Int>? {
        return candidates
    }

    fun isToggledOn(): Boolean {
        return extrasToggledOn
    }

    /**
     * Show key preview
     */
    fun showPreview() {
        val rect = view.locateView()
        popup = view.createPopup(getKeyLabel())
        popup.show(view, rect)
    }

    /**
     * Returns the label of the key
     */
    fun getKeyLabel(): String = (view as Button).text.toString() // todo use the code

    /**
     * Hide key preview
     */
    fun hidePreview() {
        popup.dismiss() // might not work
    }

}
