package com.android.lvicto.sanskriter.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapter.TitlesAdapter
import com.android.lvicto.sanskriter.util.BookHelper
import com.android.lvicto.sanskriter.util.PreferenceHelper

/**
 * Fragment holding the contents of the book
 */
class BookContentsFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var adapter: TitlesAdapter
    private lateinit var bookHelper: BookHelper
    private lateinit var prefHelper: PreferenceHelper

    private val clickListener: View.OnClickListener
        get() {
            return View.OnClickListener { view ->

                val title = view.tag as String
                prefHelper.setLastSection(title)
                if (bookHelper.isChapter(title)) {
                    Log.d(LOG_TAG, "Clicked on chapter: $title")
                    bookHelper.openChapterAt(bookHelper.getChapterIndexFromTitle(title) - 1) // zero-based indexing !!
                    adapter.data = bookHelper.titles
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
        adapter = TitlesAdapter(this.activity!!, clickListener)
        rv.adapter = adapter
        return view
    }

    override fun onResume() {
        super.onResume()
        setContents()
    }

    fun setContents(isRestoring: Boolean = false) {
        val lastSection = prefHelper.getLastSection()
        if (!isRestoring) {
            Log.d(LOG_TAG, "Creating contents... Last section: $lastSection")
            val chapter = bookHelper.getChapterIndexOfSection(lastSection)
            bookHelper.openChapterAt(chapter)
            Log.d(LOG_TAG, "setContents(): isRestoring == false $lastSection")
        }
        adapter.isSearchOn = false
        adapter.data = bookHelper.titles
    }


    fun onSearchClicked() {
        Log.d(LOG_TAG, "BookContentsFragment(): search titles")
        adapter.isSearchOn = true
        adapter.data = bookHelper.allSectionsTitles
    }

    private fun onSectionClicked(title: String) {
        listener?.onClickBookSection(title)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        bookHelper = BookHelper.getInstance()
        prefHelper = PreferenceHelper(context)

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
        adapter.data = bookHelper.filter(s)
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
