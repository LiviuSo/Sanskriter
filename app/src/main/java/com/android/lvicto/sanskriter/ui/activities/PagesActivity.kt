package com.android.lvicto.sanskriter.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.fragments.BookPagesFragment
import com.android.lvicto.sanskriter.ui.fragments.ZoomPageFragment
import com.android.lvicto.sanskriter.util.PreferenceHelper

class PagesActivity : FragmentActivity(),
        BookPagesFragment.OnFragmentInteractionListener {

    private lateinit var tvTitleBar: TextView
    private lateinit var tvIndex: TextView
    private lateinit var asset: String

    override fun onBookPagesSwipe(string: String, index: Int) {
        tvTitleBar.text = string
        tvIndex.text = (index + 1).toString()
    }

    override fun onBookPagesZoom(title: String, asset: String) {
        this.asset = asset
        setFragment(FRAGMENT_ZOOM)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

        val crtSection = PreferenceHelper(this).getLastSection()

        tvTitleBar = findViewById(R.id.tvSectionTitle)
        tvTitleBar.text = crtSection

        tvIndex = findViewById(R.id.tvPageIndex)
        tvIndex.text = "1" // todo fix (hardcoded)

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