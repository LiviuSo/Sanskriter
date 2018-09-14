package com.android.lvicto.sanskriter.ui.activities

import android.content.Intent
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import com.android.lvicto.sanskriter.R
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView


class MainActivity1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        findViewById<Button>(R.id.popupActivity).setOnClickListener {
            startActivity(Intent(this, KeyboardSelectPopup::class.java))
        }

        val popUpBtn = findViewById<Button>(R.id.popupWindow)
        popUpBtn.setOnClickListener { view ->
            Toast.makeText(this, "Window pop-up", Toast.LENGTH_SHORT).show()
            val rect = locateView(view)
            if (rect != null) {
//                Toast.makeText(this, "Window pop-up rect: ${rect.top} ${rect.bottom}", Toast.LENGTH_SHORT).show()
                val popup = PopupWindow(this@MainActivity1)
                popup.contentView = layoutInflater.inflate(R.layout.activity_keyboard_select_popup, null)
                val tv = popup.contentView.findViewById<TextView>(R.id.tvInsidePopUp)
                tv.setOnClickListener {
                    popup.dismiss()
                }
                popup.isOutsideTouchable = true
                popup.setOnDismissListener {
//                    Toast.makeText(this, "Window pop-up rect: ${tv.text}", Toast.LENGTH_SHORT).show()
                    popUpBtn.text = tv.text
                }
                popup.showAtLocation(view, Gravity.BOTTOM, rect.left, rect.bottom)
                popup.update(50, 50, 300, 80)
            }
        }

    }

    fun locateView(v: View?): Rect? {
        val locationInts = IntArray(2)
        if (v == null)
            return null
        try {
            v.getLocationOnScreen(locationInts)
        } catch (npe: NullPointerException) {
            //Happens when the view doesn't exist on screen anymore.
            return null
        }

        val location = Rect()
        location.left = locationInts[0]
        location.top = locationInts[1]
        location.right = location.left + v.width
        location.bottom = location.top + v.height
        return location
    }
}
