package com.android.lvicto.zombie

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PopUpPositioningActivity : AppCompatActivity() {

    private val LOG_TAG = "PopUpPositioning"
    private lateinit var layout: ViewGroup
    private var density: Float = 0f
    private val delay = 430L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up_positioning)
        initUI()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val scaledX = event.x / density
        val scaledY = event.y / density

        when {
            event.action == MotionEvent.ACTION_DOWN -> {
                Log.d(LOG_TAG, "onTouchEvent() down: ${event.x}, ${event.y}")
                Log.d(LOG_TAG, "onTouchEvent() down (scaled): $scaledX, $scaledY")
            }
            event.action == MotionEvent.ACTION_UP ->  {
                Log.d(LOG_TAG, "onTouchEvent() up: ${event.x}, ${event.y}")
                Log.d(LOG_TAG, "onTouchEvent() up (scaled): $scaledX, $scaledY")
            }
            event.action == MotionEvent.ACTION_MOVE -> {
                Log.d(LOG_TAG, "onTouchEvent() move: ${event.x}, ${event.y}")
                Log.d(LOG_TAG, "onTouchEvent() move (scaled): $scaledX, $scaledY")
            }
            else -> {
                Log.d(LOG_TAG, "onTouchEvent() other: ${event.x}, ${event.y}")
                Log.d(LOG_TAG, "onTouchEvent() other (scaled): $scaledX, $scaledY")
            }
        }
        return super.onTouchEvent(event)
    }

    private fun initUI() {
        val keyPreviewWidth = resources.getDimension(R.dimen.preview_width).toInt()
        val keyPreviewHeight = resources.getDimension(R.dimen.preview_height).toInt()
        layout = findViewById(R.id.popUpPositioningParent)

        layout.rootView.setOnTouchListener { v, event ->
            val x = event.x
            val y = event.y

            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    Log.d(LOG_TAG, "onTouchEvent() down: $x, $y")
                }
                event.action == MotionEvent.ACTION_UP ->  {
                    Log.d(LOG_TAG, "onTouchEvent() up: $x, $y")
                }
                event.action == MotionEvent.ACTION_MOVE -> {
                    Log.d(LOG_TAG, "onTouchEvent() move: $x, $y")
                }
                else -> {
                    Log.d(LOG_TAG, "onTouchEvent() other: $x, $y")
                }
            }
            true
        }

        // todo get button coords relative to the root
        findViewById<Button>(R.id.btn01).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn02).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn03).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn04).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn05).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn06).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn07).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn08).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn09).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn10).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn11).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn12).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
        findViewById<Button>(R.id.btn13).setOnClickListener {
            setPreview(layout, it, keyPreviewWidth, keyPreviewHeight, delayMs = delay)
        }
    }

    private fun setPreview(parent: ViewGroup, key: View, keyPreviewWidth: Int, keyPreviewHeight: Int, delayMs: Long) {
        val loc = IntArray(2)
        key.getLocationInWindow(loc)
        val popup = createAndShowPrevPopUp(parent, (key as TextView).text.toString(),
                loc[0], loc[1],
                key.height,
                keyPreviewWidth, keyPreviewHeight)
        parent.postDelayed({ // todo replace with 
            popup.dismiss()
        }, delayMs)
    }

    private fun createAndShowPrevPopUp(parent: View,
                                       text: String,
                                       x: Int, y: Int,
                                       correctionHeight: Int,
                                       width: Int, height: Int,
                                       touchableOutside: Boolean = true): PopupWindow {
        val popup = PopupWindow(this)
        popup.contentView = layoutInflater.inflate(R.layout.pop_up_key_preview, null)
        popup.isOutsideTouchable = touchableOutside
        popup.contentView.findViewById<TextView>(R.id.tvPreview).text = text
        popup.showAtLocation(parent, Gravity.NO_GRAVITY, x, y - correctionHeight)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            popup.update(width, height)
        }
        return popup
    }
}
