package com.android.lvicto.sanskriter.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.widget.Button
import android.widget.TextView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookSection
import com.android.lvicto.sanskriter.ui.fragments.PageFragment
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_PAGE_ASSET
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_PAGE_TITLE
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_SECTION
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_ZOOM_BUNDLE

class PagesActivity : FragmentActivity() {

    private val LOG_TAG = "PagesActivity"

    private lateinit var mPager: ViewPager
    private lateinit var titleBar: TextView
    private lateinit var pages: Array<String>
    private lateinit var pageIndex: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

        val section = intent.getParcelableExtra<BookSection>(EXTRA_SECTION)

        titleBar = findViewById(R.id.tvPagesTitle) // todo add truncation for long titles
        pageIndex = findViewById(R.id.tvPageIndex)
        mPager = findViewById(R.id.vpPages)

        pages = Array(section.pages.size) {
            section.pages[it]
        }
        mPager.adapter = PagesAdapter(section.name, pages, this.supportFragmentManager)
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(currentPosition: Int) {
                titleBar.text = mPager.adapter!!.getPageTitle(currentPosition)
                pageIndex.text = getPageIndex()
            }
        })
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

    private fun getPageIndex() = (mPager.currentItem + 1).toString()

    private fun getPageAsset(): String = pages[mPager.currentItem]

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

    class PagesAdapter(private val sectionTitle: String,
                       private val drawables: Array<String>,
                       fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(index: Int): Fragment {
            return PageFragment.newInstance(drawables[index])
        }

        override fun getCount(): Int {
            return drawables.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return sectionTitle
        }
    }
}