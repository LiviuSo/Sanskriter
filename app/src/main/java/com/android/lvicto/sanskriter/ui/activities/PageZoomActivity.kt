package com.android.lvicto.sanskriter.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import com.android.lvicto.sanskriter.MyApplication
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.utils.AssetsHelper.getDrawableFromAssets
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_PAGE_ASSET
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_PAGE_TITLE
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_ZOOM_BUNDLE


class PageZoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_page)

        val bundle = intent.getBundleExtra(EXTRA_ZOOM_BUNDLE)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = bundle.getString(EXTRA_PAGE_TITLE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageTextBook = findViewById<ImageView>(R.id.zoomablePage)
        imageTextBook.setImageDrawable(getDrawableFromAssets(MyApplication.application,
                bundle.getString(EXTRA_PAGE_ASSET)))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

