package com.android.lvicto.zombie.suggestion

import com.android.lvicto.zombie.keyboard.mock.SuggestionHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class TapSuggestionTests {

    @Test
    fun testSuggestionInsideWords() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 7
        val suggestion = "building"
        val result = SuggestionHelper.onTapSuggestion(sequence, index, suggestion)
        assertEquals("I am building now; it's a sanskrit keyboard.", result)
    }

    @Test
    fun testSuggestionBeginWord() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 5
        val suggestion = "building"
        val result = SuggestionHelper.onTapSuggestion(sequence, index, suggestion)
        assertEquals("I am building now; it's a sanskrit keyboard.", result)
    }

    @Test
    fun testSuggestionEndWord() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 11
        val suggestion = "building"
        val result = SuggestionHelper.onTapSuggestion(sequence, index, suggestion)
        assertEquals("I am building now; it's a sanskrit keyboard.", result)
    }

    @Test
    fun testSuggestionBetweenWords() {
        val sequence = "I am working now; it's a sanskrit keyboard."
        val index = 16
        val suggestion = "for a bit"
        val result = SuggestionHelper.onTapSuggestion(sequence, index, suggestion)
        assertEquals("I am working for a bit; it's a sanskrit keyboard.", result)
    }

}