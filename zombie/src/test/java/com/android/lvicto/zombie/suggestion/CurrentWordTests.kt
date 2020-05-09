package com.android.lvicto.zombie.suggestion

import com.android.lvicto.zombie.keyboard.mock.SuggestionHelper
import org.junit.Assert
import org.junit.Test

class CurrentWordTests {

    @Test
    fun testFindWordInside() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 7
        val wordBounds = SuggestionHelper.getCurrentWordIndices(sequence = sequence, index = index)
        Assert.assertEquals("working", sequence.substring(wordBounds.first, wordBounds.second))
    }

    @Test
    fun testFindWordBeginning() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 5
        val wordBounds = SuggestionHelper.getCurrentWordIndices(sequence = sequence, index = index)
        Assert.assertEquals("working", sequence.substring(wordBounds.first, wordBounds.second))
    }

    @Test
    fun testFindWordBeginning2() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 0
        val wordBounds = SuggestionHelper.getCurrentWordIndices(sequence = sequence, index = index)
        Assert.assertEquals("I", sequence.substring(wordBounds.first, wordBounds.second))
    }

    @Test
    fun testFindWordEnd() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 11
        val wordBounds = SuggestionHelper.getCurrentWordIndices(sequence = sequence, index = index)
        Assert.assertEquals("working", sequence.substring(wordBounds.first, wordBounds.second))
    }

    @Test
    fun testFindWordEnd2() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 15
        val wordBounds = SuggestionHelper.getCurrentWordIndices(sequence = sequence, index = index)
        Assert.assertEquals("now", sequence.substring(wordBounds.first, wordBounds.second))
    }

    @Test
    fun testFindWordEnd3() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = sequence.length - 1
        val wordBounds = SuggestionHelper.getCurrentWordIndices(sequence = sequence, index = index)
        Assert.assertEquals("keyboard", sequence.substring(wordBounds.first, wordBounds.second))
    }

    @Test
    fun testFindWordBetweenWords() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 16
        val wordBounds = SuggestionHelper.getCurrentWordIndices(sequence = sequence, index = index)
        Assert.assertEquals("now", sequence.substring(wordBounds.first, wordBounds.second))
    }
}