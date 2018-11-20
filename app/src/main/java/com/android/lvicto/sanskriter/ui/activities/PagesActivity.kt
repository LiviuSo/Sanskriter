package com.android.lvicto.sanskriter.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.Log
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookSection
import com.android.lvicto.sanskriter.ui.fragments.PageFragment

class PagesActivity : FragmentActivity() {

    private val LOG_TAG = "PagesActivity"

    private lateinit var mPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_page)

        mPager = findViewById(R.id.vpPages)
        val section = intent.getParcelableExtra<BookSection>("section")
        val pages = Array(section.pages.size) {
            section.pages[it]
        }
        mPager.adapter = PagesAdapter(pages, this.supportFragmentManager)

//        Log.d(LOG_TAG, section.name)
    }

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

    class PagesAdapter(private val drawables: Array<String>, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(index: Int): Fragment {
            return PageFragment.newInstance(drawables[index])
        }

        override fun getCount(): Int {
            return drawables.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "section $position"
        }
    }
}