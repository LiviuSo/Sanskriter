package com.android.lvicto.sanskriter.ui.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Button
import android.widget.TextView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.fragments.BookPagesFragment
import com.android.lvicto.sanskriter.ui.fragments.ZoomPageFragment
import com.android.lvicto.sanskriter.utils.PreferenceHelper

class PagesActivity : FragmentActivity(),
        BookPagesFragment.OnFragmentInteractionListener {

    private lateinit var titleBar: TextView
    private lateinit var asset: String

    override fun onBookPagesSwipe(string: String) {
        titleBar.text = string
    }

    override fun onBookPagesZoom(title: String, asset: String) {
        this.asset = asset
        setFragment(FRAGMENT_ZOOM)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

        val crtSection = PreferenceHelper(this).getLastSection()

        titleBar = findViewById(R.id.tvSectionTitle)
        titleBar.text = crtSection

        // todo load fragment
        setFragment(FRAGMENT_PAGES)

        val btnHome = findViewById<Button>(R.id.btnHome)
        btnHome.setOnClickListener {
            finish()
        }
    }

    private fun setFragment(tag: String) {
        if(tag == FRAGMENT_PAGES) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.pageFragmentHolder, BookPagesFragment.newInstance())
                    .commit()
        } else if(tag == FRAGMENT_ZOOM){
            supportFragmentManager.beginTransaction()
                    .replace(R.id.pageFragmentHolder, ZoomPageFragment.newInstance(asset))
                    .addToBackStack(FRAGMENT_ZOOM)
                    .commit()
        }
    }

    companion object {
        const val FRAGMENT_PAGES = "BookPagesFragment"
        const val FRAGMENT_ZOOM = "ZoomPageFragment"
        private const val LOG_TAG = "PagesActivity"
    }
}