package com.android.lvicto.zombie

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PopUpAutoDismissActivity : AppCompatActivity() {

    private val LOG_TAG = "PopUpAutoDismiss"

    private lateinit var layout: ViewGroup

    private var density: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up_auto_dismiss)
        layout = findViewById(R.id.popUpTestParent)
        density = resources.displayMetrics.density
        val btn = findViewById<Button>(R.id.btnPopUp)
        btn.setOnClickListener {
            Log.d(LOG_TAG, "setOnClickListener()")
            val popup = createAndShowPrevPopUp(layout, "a", 100, 500, 100, 125)
            layout.postDelayed({
                popup.dismiss()
            }, 2340)
        }
    }

    private fun createAndShowPrevPopUp(parent: View,
                                       text: String,
                                       x: Int, y: Int,
                                       width: Int, height: Int,
                                       touchableOutside: Boolean = true): PopupWindow {
        val popup = PopupWindow(this)
        popup.contentView = layoutInflater.inflate(R.layout.pop_up_key_preview, null)
        popup.isOutsideTouchable = touchableOutside
        popup.contentView.findViewById<TextView>(R.id.tvPreview).text = text
        popup.showAtLocation(parent, Gravity.NO_GRAVITY, x - width / 2, y - height / 2)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            popup.update(width, height) // todo get them from resources
        }
        return popup
    }
}
