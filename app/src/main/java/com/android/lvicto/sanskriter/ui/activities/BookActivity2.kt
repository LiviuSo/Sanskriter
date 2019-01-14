package com.android.lvicto.sanskriter.ui.activities

import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.ui.fragments.BookContentsFragment
import com.android.lvicto.sanskriter.ui.fragments.BookPagesFragment
import com.android.lvicto.sanskriter.utils.PreferenceHelper
import com.android.lvicto.sanskriter.viewmodels.ChaptersViewModel

class BookActivity2 : AppCompatActivity(),
        BookContentsFragment.OnFragmentInteractionListener,
        BookPagesFragment.OnFragmentInteractionListener {

    lateinit var viewModel: ChaptersViewModel

    override fun onClickBookSection(string: String) {
        showBookPages(string)
    }

    override fun onBookPagesInteraction(uri: Uri) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book2)
        viewModel = ChaptersViewModel()

        showBookContents()
    }

    private fun showBookContents() {
        val searchButton = findViewById<Button>(R.id.btnSearch)
        searchButton.visibility = View.VISIBLE
        searchButton.setOnClickListener {
            (supportFragmentManager.findFragmentByTag(FRAG_BOOK_CONTENTS) as BookContentsFragment).onSearchClicked()
        }
        viewModel.bookContents.observe(this, Observer<BookContent> {

            findViewById<TextView>(R.id.tvTitle).text = it?.title
        })
        supportFragmentManager.beginTransaction().add(R.id.fragmentHolder,
                BookContentsFragment.newInstance(), FRAG_BOOK_CONTENTS).commit()
    }

    private fun showBookPages(title: String) {
        findViewById<TextView>(R.id.tvTitle).text = title
        supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder,
                BookPagesFragment.newInstance(), FRAG_BOOK_PAGES).commit()
    }

    companion object {
        private const val FRAG_BOOK_CONTENTS = "BookContentsFragment"
        private const val FRAG_BOOK_PAGES = "BookContentsFragment"
    }
}