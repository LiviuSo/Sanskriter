package com.android.lvicto.sanskriter.ui.activities

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.util.Log
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapters.TitlesAdapter
import com.android.lvicto.sanskriter.viewmodels.ChaptersViewModel
import com.google.gson.Gson
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.data.BookSection
import java.io.InputStreamReader
import java.lang.StringBuilder

class BookActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    companion object {
        private val LOG_TAG = BookActivity::class.java.simpleName
    }

    private lateinit var viewModel: ChaptersViewModel // todo: expose it with setter to testing


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        // init RecyclerView
        recyclerView = this.findViewById(R.id.chapters)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // init viewmodel
        viewModel = ViewModelProviders.of(this).get(ChaptersViewModel::class.java)
        viewModel.bookContents.observe(this, Observer<BookContent> { bk ->
            recyclerView.adapter = TitlesAdapter(this, bk!!)
        })
    }

    override fun onResume() {
        super.onResume()
        (recyclerView.adapter as TitlesAdapter).openLatestVisitedChapter()
    }
}