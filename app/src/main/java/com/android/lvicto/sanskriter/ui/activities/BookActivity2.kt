package com.android.lvicto.sanskriter.ui.activities

import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.ui.fragments.BookContentsFragment
import com.android.lvicto.sanskriter.ui.fragments.BookPagesFragment
import com.android.lvicto.sanskriter.viewmodels.ChaptersViewModel

class BookActivity2 : AppCompatActivity(),
        BookContentsFragment.OnFragmentInteractionListener,
        BookPagesFragment.OnFragmentInteractionListener {

    lateinit var viewModel: ChaptersViewModel
    lateinit var btnSearch: Button
    private lateinit var searchBar: LinearLayout
    private lateinit var editSearch: EditText
    private lateinit var tvTitle: TextView
    private lateinit var bookTitle: String

    override fun onClickBookSection(string: String) {
        // hide search bar

        showBookPages(string)
    }

    override fun onBookPagesInteraction(uri: Uri) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book2)
        viewModel = ChaptersViewModel()

        setToolbar()
        showBookContents()
    }

    private fun showBookContents() {
        viewModel.bookContents.observe(this, Observer<BookContent> {
            bookTitle = it?.title.toString()
            tvTitle.text = bookTitle
            supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder,
                    BookContentsFragment.newInstance(), FRAG_BOOK_CONTENTS)
                    .commit()
        })
    }

    private fun showBookPages(title: String) {
        editSearch.text.clear()
        searchBar.visibility = View.GONE
        btnSearch.visibility = View.GONE
        tvTitle.text = title
        supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder,
                BookPagesFragment.newInstance(), FRAG_BOOK_PAGES)
                .addToBackStack("BookPagesFragment")
                .commit()
    }

    private fun setToolbar() {
        tvTitle = findViewById(R.id.tvTitle)
        searchBar = findViewById(R.id.llSearchBar)
        btnSearch = findViewById(R.id.btnSearch)
        btnSearch.visibility = View.VISIBLE
        btnSearch.setOnClickListener {
            if(searchBar.visibility == View.GONE) {
                Log.d(LOG_TAG, "Clicked search titles")
                searchBar.visibility = View.VISIBLE
                (supportFragmentManager.findFragmentByTag(FRAG_BOOK_CONTENTS) as BookContentsFragment).onSearchClicked()
            }
        }
        // init search bar
        editSearch = findViewById(R.id.editSearch)
        editSearch.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                (supportFragmentManager.findFragmentByTag(FRAG_BOOK_CONTENTS) as BookContentsFragment).filterSectionTitles(s.toString())
            }
        })
        val btnCloseSearch = findViewById<ImageButton>(R.id.btnCloseSearchBar)
        btnCloseSearch.setOnClickListener {
            searchBar.visibility = View.GONE
            (supportFragmentManager.findFragmentByTag(FRAG_BOOK_CONTENTS) as BookContentsFragment).setContents(true)
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.findFragmentByTag(FRAG_BOOK_PAGES) is BookPagesFragment) {
            Log.d(LOG_TAG, "back from FRAG_BOOK_PAGES")
            btnSearch.visibility = View.VISIBLE
            tvTitle.text = bookTitle
        } else if(supportFragmentManager.findFragmentByTag(FRAG_BOOK_CONTENTS) is BookContentsFragment) {
            Log.d(LOG_TAG, "back from FRAG_BOOK_CONTENTS")
        }
        super.onBackPressed()
    }

    companion object {
        private const val FRAG_BOOK_CONTENTS = "BookContentsFragment"
        private const val FRAG_BOOK_PAGES = "BookContentsFragment"
        private const val LOG_TAG = "BookActivity2"
    }
}