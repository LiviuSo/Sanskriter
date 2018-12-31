package com.android.lvicto.sanskriter.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookPage
import com.android.lvicto.sanskriter.utils.AssetsHelper.getDrawableFromAssets

class PageFragment : Fragment() {

    private var drawablePath: String = ""

    companion object {

        private const val LOG_TAG = "PageFragment"

        fun newInstance(page: BookPage): PageFragment {
            val fragment = PageFragment()
            fragment.drawablePath = page.asset
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_pages, container, false)
        val imageTextBook = view.findViewById<ImageView>(R.id.textBookCut)
        imageTextBook.setImageDrawable(getDrawableFromAssets(application, drawablePath))
        return view
    }

    // todo move into viewmodel and refactor to arch
}