package com.android.lvicto.sanskriter.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.util.BookHelper
import com.android.lvicto.sanskriter.util.AssetsHelper
import com.android.lvicto.sanskriter.util.PreferenceHelper


/**
 * Shows the pages of the book
 */
class BookPagesFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var gd: GestureDetector
    private lateinit var bookHelper: BookHelper
    private lateinit var prefHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sectionTitle = PreferenceHelper(requireActivity()).getLastSection()
        val pageIndexInSection = 0 // todo save last page index
        bookHelper.setCurrentPage(sectionTitle, pageIndexInSection)
        gd = GestureDetector(requireActivity().applicationContext, GestureListener())
    }

    private lateinit var pageImageView: ImageView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_book_pages, container, false)
        pageImageView = view.findViewById(R.id.ivPage)
        pageImageView.setOnTouchListener { v, event ->
            gd.onTouchEvent(event)
        }

        val asset = bookHelper.currentPage.asset
        AssetsHelper.loadAsset(pageImageView, asset)
        val btnZoom = view.findViewById<Button>(R.id.btnZoomPage)
        btnZoom.setOnClickListener(this::onZoom)

        return view
    }

    private fun onZoom(view: View) {
        val sectionTitle = bookHelper.currentPage.sectionName
        val asset = bookHelper.currentPage.asset
        listener?.onBookPagesZoom(sectionTitle, asset)
    }

    fun onSWipe(title: String, index: Int) {
        listener?.onBookPagesSwipe(title, index)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        bookHelper = BookHelper.getInstance()
        prefHelper = PreferenceHelper(context)

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onBookPagesSwipe(string: String, index: Int)
        fun onBookPagesZoom(title: String, asset: String)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BookPagesFragment()

        val LOG_TAG: String = BookPagesFragment::class.java.simpleName
    }

    inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }

        private fun onSwipeRight() {
            Log.d(LOG_TAG, "onSwipeRight: next page")
            bookHelper.setPrevPage()
            setPage()
        }

        private fun onSwipeLeft() {
            Log.d(LOG_TAG, "onSwipeLeft: prev page")
            bookHelper.setNextPage()
            setPage()
        }

        private fun setPage() {
            val crtPage = bookHelper.currentPage
            AssetsHelper.loadAsset(pageImageView, crtPage.asset)
            prefHelper.setLastSection(crtPage.sectionName)
            onSWipe(crtPage.sectionName, crtPage.indexInSection)
        }
    }
}