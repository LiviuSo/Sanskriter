package com.lvicto.skeyboard.service

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import com.lvicto.skeyboard.Constants

/**
 * Wrapper around the InputConnection si EditorInfo of some inputMethodService
 * @param ic: InputConnection
 * @param ei: EditorInfo
 */
class InputConnectionWrapper2( var ic: InputConnection? = null,
                               var ei: EditorInfo? = null) : InputConnectionWrapper(ic, true) {
    val imeActionLabel: String
        get() = ei?.actionLabel.toString()

    private var selectionStart = 0
    private var selectionStartOld = 0
    private var selectionEnd = 0
    private var selectionEndOld = 0

    fun deleteSurroundingText() {
        val lenBefore = getBeforeCursorInSurroundingWord().length
        val lengthAfter = getAfterCursorInSurroundingWord().length
        ic?.deleteSurroundingText(lenBefore, lengthAfter)
    }

    fun getBeforeCursorInSurroundingWord(): String {
        val before = ic?.getTextBeforeCursor(Constants.MAX_INPUT_LEN, 0)
        if (before.isNullOrEmpty()) {
            return ""
        }
        return StringBuffer().append(before.takeLastWhile {
            it != ' '
        }).toString()
    }

    private fun getAfterCursorInSurroundingWord(): String {
        val after = ic?.getTextAfterCursor(Constants.MAX_INPUT_LEN, 0)
        if (after.isNullOrEmpty()) {
            return ""
        }
        return StringBuffer()
                .append(after.takeWhile {
                    it != ' '
                }).toString()
    }

    fun isLastWord(): Boolean {
        val se = ic?.getTextAfterCursor(Constants.MAX_INPUT_LEN, 0) ?: return true
        val parts = se.split(' ')
        return parts.size < 2
    }

    fun deleteCurrentSelection() {
        if (selectionEnd > selectionStart) {
            ic?.setComposingRegion(selectionStart, selectionEnd)
            ic?.setComposingText("", 1)
        } else {
            ic?.deleteSurroundingText(1, 0)
        }
    }

    /**
     * Update the selection indexes
     */
    fun updateSelection(start: Int, end: Int, startOld: Int, endOld:Int) {
        selectionStart = start
        selectionStartOld = startOld
        selectionEnd = end
        selectionEndOld = endOld
    }

    fun commitText(newText: String) {
        ic?.commitText(newText, 1 )
    }


    fun isBeginningOfSentence(): Boolean {
        val beforeText = ic?.getTextBeforeCursor(Constants.MAX_INPUT_LEN, 0)
        return if(beforeText != null) {
            val len = beforeText.length
            when {
                len > 1 -> {
                    beforeText[len - 1] == ' ' && beforeText[len - 2] == '.'
                }
                len > 0 -> {
                    beforeText[len - 1] == ' '
                }
                else -> {
                    true
                }
            }
        } else {
            false
        }
    }


}
