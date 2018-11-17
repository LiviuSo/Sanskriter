package com.android.lvicto.sanskriter.ui.fragments

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.sanskriter.R
import com.jsibbold.zoomage.ZoomageView

class PageFragment : Fragment() {


    companion object {
        private var sDrawable: Int = 0

        fun newInstance(@IdRes drawable: Int): PageFragment {
           sDrawable = drawable
            return PageFragment()
        }

    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.activity_page, container, false)
        val imageTextBook = view.findViewById<ZoomageView>(R.id.textBookCut)
        imageTextBook.setImageDrawable(ContextCompat.getDrawable(this.activity!!,
                sDrawable))
        return view
    }
}