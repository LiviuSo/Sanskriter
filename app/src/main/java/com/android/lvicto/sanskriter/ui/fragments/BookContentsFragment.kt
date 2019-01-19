package com.android.lvicto.sanskriter.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapters.TitlesAdapter2
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.source.BookContentHelper
import com.android.lvicto.sanskriter.utils.PreferenceHelper
import com.android.lvicto.sanskriter.viewmodels.ChaptersViewModel

/**
 * Fragment holding the contents of the book
 */
class BookContentsFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var adapter: TitlesAdapter2
    private lateinit var viewModel: ChaptersViewModel // todo: expose it with setter to testing
    private lateinit var bookContentHelper: BookContentHelper

    private val clickListener: View.OnClickListener
        get() {
            return View.OnClickListener { view ->

                val title = view.tag as String
                PreferenceHelper(this.activity!!).setLastSection(title)
                if (bookContentHelper.isChapter(title)) {
                    Log.d(LOG_TAG, "Clicked on chapter: $title")
                    bookContentHelper.openChapterAt(bookContentHelper.getChapterIndexFromTitle(title) - 1) // zero-based indexing !!
                    adapter.data = bookContentHelper.titles
                } else {
                    Log.d(LOG_TAG, "Clicked on section: $title")
                    onSectionClicked(title)
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_book_contents, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.recViewTitles)
        rv.layoutManager = LinearLayoutManager(this.activity)
        viewModel.bookContents.observe(this, Observer<BookContent> {
            // create a sections list
            bookContentHelper = BookContentHelper.getInstance()
            adapter = TitlesAdapter2(this.activity!!, clickListener)
            rv.adapter = adapter
            setContents()
        })
        return view
    }

    fun setContents(isRestoring: Boolean = false) {
        val lastSection = PreferenceHelper(this.activity!!).getLastSection()
        if (!isRestoring) {
            Log.d(LOG_TAG, "Creating contents... Last section: $lastSection")
            val chapter = bookContentHelper.getChapterIndexOfSection(lastSection)
            bookContentHelper.openChapterAt(chapter)
        }
        adapter.isSearchOn = false
        adapter.data = bookContentHelper.titles
    }


    fun onSearchClicked() {
        Log.d(LOG_TAG, "BookContentsFragment(): search titles")
        adapter.isSearchOn = true
        adapter.data = bookContentHelper.allSections
    }

    private fun onSectionClicked(title: String) {
        listener?.onClickBookSection(title)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this).get(ChaptersViewModel::class.java)

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun filterSectionTitles(s: String) {
        Log.d(LOG_TAG, "Filtering title... $s")
        adapter.data = bookContentHelper.filter(s)
    }

    interface OnFragmentInteractionListener {
        fun onClickBookSection(string: String)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BookContentsFragment()

        private const val LOG_TAG = "BookContentsFragment"
    }

}
