package com.android.lvicto.sanskriter.util

import com.android.lvicto.sanskriter.MyApplication
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.data.BookSection
import com.android.lvicto.sanskriter.data.book.BookPage

/**
 * In charge of the logic of the titles
 */
class BookHelper {

    var title: String = ""
    var titles: ArrayList<String> = arrayListOf()
    var allSectionsTitles: ArrayList<String> = arrayListOf()

    private lateinit var pages: ArrayList<BookPage>
    private var currentPageIndex: Int = -1
    lateinit var currentPage: BookPage

    private var currentlyExpanded: Int = -1
    private lateinit var sections: Map<Int, List<BookSection>>

    companion object {
        private val defBookContent = BookContent("No book", 0, mapOf())
        private var instance: BookHelper? = null

        fun getInstance(): BookHelper {
            if (instance != null) {
                return instance as BookHelper
            }
            return synchronized(this) {
                // in case invoked on some different thread
                val i2 = instance
                if (i2 != null) {
                    i2
                } else {
                    instance = BookHelper()
                    instance!!.setData(defBookContent)
                    instance!!
                }
            }
        }
    }

    fun setData(bookContents: BookContent): BookHelper {
        synchronized(this) {
            instance!!.title = bookContents.title
            instance!!.sections = bookContents.sections
            instance!!.generateChapterTitles()
            instance!!.initAllSectionsTitles()
            createPages()
            return instance as BookHelper
        }
    }

    fun openChapterAt(position: Int) {
        if (position == -1) {
            return
        }
        if (!isExpanded(position)) {
            expandData(position)
        }
    }

    private fun expandData(position: Int) {
        assert(position >= 0 && position < titles.size)

        if (currentlyExpanded != -1) { // collapse the previously expanded
            collapseData(currentlyExpanded)
        }
        // remove each corresponding subsection title
        val sections = sections[position]
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
        val sections = sections[position]

        (1..sections!!.size).forEach { _ ->
            titles.removeAt(position + 1)
        }
        currentlyExpanded = -1
    }

    private fun isExpanded(position: Int): Boolean = position == currentlyExpanded

    private fun generateChapterTitles(): ArrayList<String> {
        (1..sections.keys.size).forEach {
            titles.add("${MyApplication.application.getString(R.string.chapter)} $it")
        }
        return titles
    }

    fun getChapterIndexOfSection(section: String): Int {
        var index = -1
        mainLoop@ for (i in 0 until sections.size) {
            for (bs in sections[i]!!) {
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

    private fun initAllSectionsTitles() {
        allSectionsTitles.clear()
        sections.forEach {
            it.value.forEach { bs ->
                allSectionsTitles.add(bs.name)
            }
        }
    }

    fun filter(key: String): ArrayList<String> {
        return arrayListOf<String>().apply {
            addAll(allSectionsTitles.filter {
                it.contains(key)
            })
        }
    }

    fun createPages() {
        pages = ArrayList()
        (0 until sections.keys.size).forEach { chapterIndex ->
            sections[chapterIndex]?.forEach { section ->
                (0 until (section.pages?.size ?: 0)).forEach { pageIndex ->
                    section.pages?.get(pageIndex)?.let { BookPage(chapterIndex, section.name, pageIndex, it) }?.let { pages.add(it) }
                }
            }
        }
    }

    fun setCurrentPage(section: String, pageIndexInSection: Int) {
        currentPageIndex = getIndex(section, pageIndexInSection)
        currentPage = pages[currentPageIndex]
    }

    fun setNextPage() {
        if(currentPageIndex < pages.size - 1) {
            currentPageIndex = currentPageIndex.inc()
        }
        currentPage = pages[currentPageIndex]
    }

    fun setPrevPage() {
        if(currentPageIndex > 0) {
            currentPageIndex = currentPageIndex.dec()
        }
        currentPage = pages[currentPageIndex]
    }

    private fun getIndex(section: String, pageIndexInSection: Int): Int {
        return (0 until pages.size).first {
            val page = pages[it]
            page.sectionName == section && page.indexInSection == pageIndexInSection
        }
    }
}
