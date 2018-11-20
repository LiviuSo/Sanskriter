package com.android.lvicto.sanskriter.ui.fragments

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.R
import com.jsibbold.zoomage.ZoomageView
import java.lang.Exception

class PageFragment : Fragment() {

    private var drawablePath: String = ""

    companion object {

        private const val LOG_TAG = "PageFragment"

        fun newInstance(page: String): PageFragment {
            val fragment = PageFragment()
            fragment.drawablePath = page
            return fragment
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_pages, container, false)
        val imageTextBook = view.findViewById<ImageView>(R.id.textBookCut)
        imageTextBook.setImageDrawable(getDrawableFromAssets(application, drawablePath))
        return view
    }

    // todo move into viewmodel and refactor to arch
    private fun getDrawableFromAssets(context: Context, path: String): Drawable? =
            if (path.isEmpty()) {
                null
            } else {
                try {
                    BitmapDrawable.createFromStream(context.assets.open(path), null)
                } catch (e: Exception) {
                    Log.d(LOG_TAG, "PageFragment::getDrawableFromAssets() + ${e.message}")
                    null
                }
            }
}