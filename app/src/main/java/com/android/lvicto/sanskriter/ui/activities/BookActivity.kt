package com.android.lvicto.sanskriter.ui.activities

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.arch.lifecycle.ViewModelProviders
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapters.TitlesAdapter
import com.android.lvicto.sanskriter.viewmodels.ChaptersViewModel
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.db.entity.Word
import com.android.lvicto.sanskriter.utils.PreferenceHelper

class BookActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    companion object {
        private val LOG_TAG = BookActivity::class.java.simpleName
    }

    private lateinit var viewModel: ChaptersViewModel // todo: expose it with setter to testing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        // init toolbar
        val titleBar = findViewById<TextView>(R.id.tvTitle)
        findViewById<TextView>(R.id.tvPageIndex).visibility = View.GONE
        findViewById<Button>(R.id.btnHome).visibility = View.GONE

        // init RecyclerView
        recyclerView = this.findViewById(R.id.chapters)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // init viewmodel
        viewModel = ViewModelProviders.of(this).get(ChaptersViewModel::class.java)
        viewModel.bookContents.observe(this, Observer<BookContent> { bk ->
            recyclerView.adapter = TitlesAdapter(this, bk!!)
            titleBar.text = bk.title
        })

        // init search bar
        val searchBar = findViewById<LinearLayout>(R.id.llSearchBar)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        btnSearch.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                Log.d(LOG_TAG, "search titles")
                searchBar.visibility = View.VISIBLE
            }
        }
        val editSearch = findViewById<EditText>(R.id.editSearch)
        editSearch.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filter(s.toString()).observe(this@BookActivity, Observer<List<Word>>{
                    Log.d(LOG_TAG, "Filtering... $s")
                })
            }
        })
        val btnCloseSearch = findViewById<ImageButton>(R.id.btnCloseSearchBar)
        btnCloseSearch.setOnClickListener {
            searchBar.visibility = View.GONE
            // todo clear results
        }
    }

    override fun onResume() {
        super.onResume()
        (recyclerView.adapter as TitlesAdapter).openLatestVisitedChapter()
    }
}