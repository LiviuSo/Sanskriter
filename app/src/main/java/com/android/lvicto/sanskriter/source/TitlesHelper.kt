package com.android.lvicto.sanskriter.source

import android.content.Context
import com.android.lvicto.sanskriter.MyApplication
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.data.BookPage
import com.android.lvicto.sanskriter.data.BookSection
import com.android.lvicto.sanskriter.utils.PreferenceHelper

/**
 * Returns the titles/headers of the chapters/sections
 */
class TitlesHelper(private val bookContent: BookContent) { // todo: write unit tests

    lateinit var titles: ArrayList<String>
    private var sectionTitles: Map<Int, List<BookSection>> = bookContent.sections

    companion object {
        var lastOpenedSectionTitle: String = ""
        var currentlyExpanded: Int = -1
        var futureChapter: Int = -1
        var currentChapter: Int = -1
    }

    // todo fix
    fun getLastAccessedSection(context: Context): String =
            PreferenceHelper(context).getLastSection()

    // todo fix
    fun setLastAccessedSection(context: Context, title: String) {
        PreferenceHelper(context).setLastSection(title)
    }

    init {
        generateChapterTitles()
    }

    fun expandData(position: Int) {
        assert(position >= 0 && position < titles.size)

        var positionToExpand = position
        if (currentlyExpanded != -1) { // collapse the previously expanded
            if (currentlyExpanded < position) { // adjust the position to expand
                val sectionCurrentlyExpanded = sectionTitles[currentlyExpanded]
                positionToExpand -= sectionCurrentlyExpanded!!.size
            }
            collapseData(currentlyExpanded)
        }

        // remove each corresponding subsection title
        val sections = sectionTitles[positionToExpand]
        if (positionToExpand < titles.size - 1) {
            titles.addAll(positionToExpand + 1, sections!!.map { it.name })
        } else {
            sections!!.forEach {
                titles.add(it.name)
            }
        }
        currentlyExpanded = positionToExpand
    }

    fun collapseData(position: Int) {
        assert(position == currentlyExpanded)
        assert(position >= 0 && position < titles.size)

        // remove each corresponding subsection title
        val sections = sectionTitles[position]

        (1..sections!!.size).forEach { _ ->
            titles.removeAt(position + 1)
        }
        currentlyExpanded = -1
    }

    fun isExpanded(position: Int): Boolean = position == currentlyExpanded

    override fun toString(): String {
        val buffer = StringBuffer("[")
        titles.forEach {
            buffer.append(it).append(" ")
        }
        buffer.append(" ")
        return buffer.toString()
    }

    private fun generateChapterTitles(): ArrayList<String> {
        titles = arrayListOf() // todo find how to access layered it with(arrayListOf()) { (0..2).forEach() { ....}}
        (1..sectionTitles.keys.size).forEach {
            titles.add("${MyApplication.application.getString(R.string.chapter)} $it")
        }
        return titles
    }

    fun createPages(): List<BookPage> {
        val pages = ArrayList<BookPage>()
        (0 until bookContent.sections.keys.size).forEach { chapterIndex ->
            bookContent.sections[chapterIndex]?.forEach { section ->
                (0 until section.pages.size).forEach { pageIndex ->
                    pages.add(BookPage(chapterIndex, section.name, pageIndex, section.pages[pageIndex]))
                }
            }
        }
        return pages
    }

    fun getChapterIndexOfSection(lastOpenedSectionTitle: String): Int {
        return (0 until bookContent.sections.keys.size).first {
            bookContent.sections[it]?.firstOrNull { bs ->
                bs.name == lastOpenedSectionTitle
            } != null
        }
    }

    fun getSectionsCountOfChapter(chapterIndex: Int): Int = sectionTitles[chapterIndex]!!.size
}