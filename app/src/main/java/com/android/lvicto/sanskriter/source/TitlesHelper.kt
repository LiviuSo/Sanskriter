package com.android.lvicto.sanskriter.source

import com.android.lvicto.sanskriter.MyApplication
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.data.BookSection

/**
 * Returns the titles/headers of the chapters/sections
 */
class TitlesHelper(bookContent: BookContent) { // todo: write unit tests

    lateinit var titles: ArrayList<String>
    var sectionTitles: Map<Int, List<BookSection>> = bookContent.sections
    private var currentlyExpanded: Int = -1

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

        (1..sections!!.size).forEach {
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

    fun generateChapterTitles(): ArrayList<String> {
        titles = arrayListOf() // todo find how to access layered it with(arrayListOf()) { (0..2).forEach() { ....}}
        (1..sectionTitles.keys.size).forEach {
            titles.add("${MyApplication.application.getString(R.string.chapter)} $it")
        }
        return titles
    }

    fun getSectionByTitle(title: String): BookSection? {
        if(currentlyExpanded == -1) {
            return null
        }
        return sectionTitles[currentlyExpanded]!!.first {
            it.name == title
        }
    }
}