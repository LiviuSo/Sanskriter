package com.android.lvicto.sanskriter.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.util.BookHelper
import com.android.lvicto.sanskriter.ui.fragments.BookContentsFragment
import com.android.lvicto.sanskriter.viewmodel.ChaptersViewModel

class BookActivity : AppCompatActivity(),
        BookContentsFragment.OnFragmentInteractionListener {

    lateinit var viewModel: ChaptersViewModel
    lateinit var btnSearch: Button
    private lateinit var searchBar: LinearLayout
    private lateinit var editSearch: EditText
    private lateinit var tvTitle: TextView

    override fun onClickBookSection(string: String) {
        editSearch.text.clear()
        searchBar.visibility = View.GONE
        val intent = Intent(this, PagesActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book)
        viewModel = ChaptersViewModel()

        setToolbar()
        showBookContents()
    }

    private fun showBookContents() {
        viewModel.bookContents.observe(this, Observer<BookContent> {
            BookHelper.getInstance().setData(it!!) // todo refactor to init()
            tvTitle.text = it.title
            supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder,
                    BookContentsFragment.newInstance(), FRAG_BOOK_CONTENTS)
                    .commit()
        })
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

    companion object {
        private const val FRAG_BOOK_CONTENTS = "BookContentsFragment"
        private const val FRAG_BOOK_PAGES = "BookContentsFragment"
        private const val LOG_TAG = "BookActivity"
    }
}