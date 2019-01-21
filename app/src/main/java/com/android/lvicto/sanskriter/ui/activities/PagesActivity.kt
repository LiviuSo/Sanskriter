package com.android.lvicto.sanskriter.ui.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Button
import android.widget.TextView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.fragments.BookPagesFragment
import com.android.lvicto.sanskriter.utils.PreferenceHelper

class PagesActivity : FragmentActivity(),
        BookPagesFragment.OnFragmentInteractionListener {

    override fun onBookPagesInteraction(string: String) {
        titleBar.text = string
    }

    private val LOG_TAG = "PagesActivity"

    private lateinit var titleBar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

        val crtSection = PreferenceHelper(this).getLastSection()

        titleBar = findViewById(R.id.tvSectionTitle)
        titleBar.text = crtSection

        // todo load fragment
        setFragment()

        val btnHome = findViewById<Button>(R.id.btnHome)
        btnHome.setOnClickListener {
            finish()
        }
    }

    private fun setFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.pageFragmentHolder, BookPagesFragment.newInstance())
                .commit()
    }
}