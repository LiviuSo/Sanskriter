package com.android.lvicto.sanskriter.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.source.TitlesHelper
import com.android.lvicto.sanskriter.ui.fragments.PageFragment
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_CONTENT
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_PAGE_ASSET
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_PAGE_TITLE
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_SECTION
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_ZOOM_BUNDLE

class PagesActivity : FragmentActivity() {

    private val LOG_TAG = "PagesActivity"

    private lateinit var mPager: ViewPager
    private lateinit var titleBar: TextView
    private lateinit var pageIndex: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

        val crtSection = intent.getStringExtra(EXTRA_SECTION)
        val bookContent = intent.getParcelableExtra<BookContent>(EXTRA_CONTENT)

        titleBar = findViewById(R.id.tvPagesTitle)
        pageIndex = findViewById(R.id.tvPageIndex)
        mPager = findViewById(R.id.vpPages)

        mPager.adapter = PagesAdapter(bookContent, this.supportFragmentManager)
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(currentPosition: Int) {
                val pagesAdapter = mPager.adapter as PagesAdapter
                val page = pagesAdapter.pages[currentPosition]

                titleBar.text = page.sectionName
                pageIndex.text = getPageIndex()

                TitlesHelper.lastOpenedSectionTitle = page.sectionName
                TitlesHelper.futureChapter = pagesAdapter.titlesHelper.getChapterIndexOfSection(page.sectionName)
                Log.d(LOG_TAG, "${page.sectionName} chapter: ${page.chapterIndex}")
            }
        })
        mPager.currentItem = (mPager.adapter as PagesAdapter).getItemPositionFromTitle(crtSection)

        titleBar.text = getPageTitle()
        pageIndex.text = getPageIndex()

        val btnHome = findViewById<Button>(R.id.btnBookHome)
        btnHome.setOnClickListener {
            finish()
        }

        val btnZoom = findViewById<Button>(R.id.btnZoomPage)
        btnZoom.setOnClickListener {
            val intent = Intent(this, PageZoomActivity::class.java)
            val bundle = Bundle()
            bundle.putString(EXTRA_PAGE_TITLE, getPageTitle())
            bundle.putString(EXTRA_PAGE_ASSET, getPageAsset())
            intent.putExtra(EXTRA_ZOOM_BUNDLE, bundle)
            startActivity(intent)
        }
    }

    private fun getPageIndex() = (mPager.adapter as PagesAdapter).getPageIndexInSection(mPager.currentItem).toString()

    private fun getPageAsset(): String = (mPager.adapter as PagesAdapter).getCurrentPageAsset(mPager.currentItem)

    private fun getPageTitle() = (mPager.adapter as PagesAdapter).getPageTitle(mPager.currentItem).toString()

    override fun onBackPressed() {
        if (mPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            mPager.currentItem = mPager.currentItem - 1
        }
    }

    class PagesAdapter(bookContent: BookContent, fm: FragmentManager)
        : FragmentStatePagerAdapter(fm) {

        val titlesHelper = TitlesHelper(bookContent)
        val pages = titlesHelper.createPages()

        override fun getItem(index: Int): Fragment = PageFragment.newInstance(pages[index])

        override fun getCount(): Int = pages.size

        override fun getPageTitle(position: Int): CharSequence? = pages[position].sectionName

        fun getCurrentPageAsset(index: Int): String = pages[index].asset

        fun getItemPositionFromTitle(crtSection: String): Int = pages.indexOfFirst {
            it.sectionName == crtSection
        }

        fun getPageIndexInSection(index: Int): Int = pages[index].indexInSection
    }
}