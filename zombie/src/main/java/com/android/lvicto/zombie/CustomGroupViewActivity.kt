package com.android.lvicto.zombie

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class CustomGroupViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_group_view)

        val tagLayout: TagLayout = findViewById(R.id.tagLayout)
        val layoutInflater = layoutInflater
        var tag: String
        for (i in 0..20) {
            tag = "#tag$i"
            val tagView: View = layoutInflater.inflate(R.layout.tag_layout, null, false)
            val tagTextView = tagView.findViewById(R.id.tagTextView) as TextView
            tagTextView.text = tag
            tagLayout.addView(tagView)
        }
    }
}
