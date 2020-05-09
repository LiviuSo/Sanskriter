package com.android.lvicto.zombie.keyboard.mock

object SuggestionHelper {

    private const val SEPARATORS = " ;.,-'()[]{}|/ "

    // inject the suggestion provider for testing

    fun completeWordWithSuggestion() {
        // todo

    }

    fun onTapSuggestion(sequence: String, index: Int, suggestion: String): String {
        val wordStartEnd = getCurrentWordIndices(sequence, index)
        return replaceCurrentWord(sequence, wordStartEnd.first, wordStartEnd.second, suggestion)
    }

    fun getCurrentWordIndices(sequence: String, index: Int): Pair<Int, Int> {
        var start = index - 1
        var end = index
        val length = sequence.length

        while(start in 1 until length) {
            if(!SEPARATORS.contains(sequence[start])) {
                start--
            } else {
                start++ // move just after the first separator found
                break
            }
        }
        while(end in 0 until length && !SEPARATORS.contains(sequence[end])) {
            end++
        }
        if(start < 0) {
            start = 0
        }
        if(start > end) {
            start = end
        }

        return Pair(start, end)
    }

    fun getCurrentWord(sequence: String, index: Int): String {
        val indices = getCurrentWordIndices(sequence, index)
        return sequence.substring(indices.first, index)
    }

    private fun replaceCurrentWord(sequence: String, start: Int, end: Int, suggestion: String): String {
        val sb = StringBuffer(sequence)
        sb.replace(start, end, suggestion)
        return sb.toString()
    }

}