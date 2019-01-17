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
import com.android.lvicto.sanskriter.MyApplication
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapters.TitlesAdapter2
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.data.BookSection
import com.android.lvicto.sanskriter.utils.PreferenceHelper
import com.android.lvicto.sanskriter.viewmodels.ChaptersViewModel
import java.util.function.Predicate
import kotlin.collections.ArrayList

/**
 * Fragment holding the contents of the book
 */
class BookContentsFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var adapter: TitlesAdapter2
    private lateinit var viewModel: ChaptersViewModel // todo: expose it with setter to testing
    private lateinit var titlesHelper: TitlesHelper

    private val clickListener: View.OnClickListener
        get() {
            return View.OnClickListener { view ->

                val title = view.tag as String
                PreferenceHelper(this.activity!!).setLastSection(title)
                if (titlesHelper.isChapter(title)) {
                    Log.d(LOG_TAG, "Clicked on chapter: $title")
                    titlesHelper.openChapterAt(titlesHelper.getChapterIndexFromTitle(title) - 1) // zero-based indexing !!
                    adapter.data = titlesHelper.titles
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
            titlesHelper = TitlesHelper(it!!)
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
            val chapter = titlesHelper.getChapterIndexOfSection(lastSection)
            titlesHelper.openChapterAt(chapter)
        }
        adapter.isSearchOn = false
        adapter.data = titlesHelper.titles
    }


    fun onSearchClicked() {
        Log.d(LOG_TAG, "BookContentsFragment(): search titles")
        adapter.isSearchOn = true
        adapter.data = titlesHelper._allSections
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
        adapter.data = titlesHelper.filter(s)
    }

    interface OnFragmentInteractionListener {
        fun onClickBookSection(string: String)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BookContentsFragment()

        private const val LOG_TAG = "BookContentsFragment"
    }

    /**
     * In charge of the logic of the titles
     */
    class TitlesHelper(bookContents: BookContent) {

        var titles: ArrayList<String> = arrayListOf()
        var _saved: ArrayList<String> = arrayListOf()
        var _allSections: ArrayList<String> = arrayListOf()

        private var sectionTitles: Map<Int, List<BookSection>> = bookContents.sections
        private var currentlyExpanded: Int = -1


        init {
            generateChapterTitles()
            initAllSections()
        }

        fun openChapterAt(position: Int) {
            if (position == -1) {
                return
            }
            if (isExpanded(position)) {
                collapseData(position)
            } else {
                expandData(position)
            }
        }

        private fun expandData(position: Int) {
            assert(position >= 0 && position < titles.size)

            if (currentlyExpanded != -1) { // collapse the previously expanded
                collapseData(currentlyExpanded)
            }
            // remove each corresponding subsection title
            val sections = sectionTitles[position]
            if (position < titles.size - 1) {
                titles.addAll(position + 1, sections!!.map { it.name })
            } else {
                sections!!.forEach {
                    titles.add(it.name)
                }
            }
            currentlyExpanded = position
        }

        private fun collapseData(position: Int) {
            assert(position == currentlyExpanded)
            assert(position >= 0 && position < titles.size)

            // remove each corresponding subsection title
            val sections = sectionTitles[position]

            (1..sections!!.size).forEach { _ ->
                titles.removeAt(position + 1)
            }
            currentlyExpanded = -1
        }

        private fun isExpanded(position: Int): Boolean = position == currentlyExpanded

        private fun generateChapterTitles(): ArrayList<String> {
            (1..sectionTitles.keys.size).forEach {
                titles.add("${MyApplication.application.getString(R.string.chapter)} $it")
            }
            return titles
        }

        fun getChapterIndexOfSection(section: String): Int {
            var index = -1
            mainLoop@ for (i in 0 until sectionTitles.size) {
                for (bs in sectionTitles[i]!!) {
                    if (bs.name == section) {
                        index = i
                        break@mainLoop
                    }
                }
            }
            return index
        }

        fun getChapterIndexFromTitle(title: String): Int {
            return title.removePrefix("Chapter ").toInt()
        }

        fun isChapter(title: String): Boolean = title.contains("Chapter")

        fun saveTitles() {
            _saved.clear()
            _saved.addAll(titles)
        }

        private fun initAllSections() {
            _allSections.clear()
            sectionTitles.forEach {
                it.value.forEach { bs ->
                    _allSections.add(bs.name)
                }
            }
        }

        fun filter(key: String):  ArrayList<String> {
            return arrayListOf<String>().apply {
                addAll(_allSections.filter {
                    it.contains(key)
                })
            }
        }
    }
}
