package com.android.lvicto.sanskriter.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.lvicto.sanskriter.MyApplication

import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.book.BookPage
import com.android.lvicto.sanskriter.source.BookHelper
import com.android.lvicto.sanskriter.utils.AssetsHelper
import com.android.lvicto.sanskriter.utils.PreferenceHelper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Shows the pages of the book
 */
class BookPagesFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    lateinit var asset: String
    lateinit var sectionTitle: String
    private var pageIndexInSection: Int = 0
    lateinit var pages: List<BookPage>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sectionTitle = PreferenceHelper(this.activity!!).getLastSection()
        pages = BookHelper.getInstance().createPages()
        pageIndexInSection = 0 // todo save last page index
        asset = getAsset(sectionTitle, pageIndexInSection)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_book_pages, container, false)
        val pageView = view.findViewById<ImageView>(R.id.ivPage)
        pageView.setOnTouchListener { v, event ->
            // todo complete
            true
        }
        pageView.setImageDrawable(AssetsHelper.getDrawableFromAssets(MyApplication.application, asset))
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onBookPagesInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

    private fun getAsset(section: String, pageIndex: Int) =
        pages.first {
            it.sectionName == section && it.indexInSection == pageIndex
        }.asset

    interface OnFragmentInteractionListener {
        fun onBookPagesInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BookPagesFragment()
    }
}
