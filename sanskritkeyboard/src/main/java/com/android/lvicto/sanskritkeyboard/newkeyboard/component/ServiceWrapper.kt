package com.android.lvicto.sanskritkeyboard.newkeyboard.component

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import com.android.lvicto.sanskritkeyboard.utils.Constants

/**
 * Wrapper around the InputConnection si EditorInfo of some inputMethodService
 * @param ic: InputConnection
 * @param ei: EditorInfo
 */
class ServiceWrapper(private val ic: InputConnection,
                     val ei: EditorInfo) : InputConnectionWrapper(ic, true) {
    private var selectionStart = 0
    private var selectionStartOld = 0
    private var selectionEnd = 0
    private var selectionEndOld = 0

    fun getBeforeCursorInSurroundingWord(): String {
        val before = ic.getTextBeforeCursor(Constants.MAX_INPUT_LEN, 0)
        if (before.isNullOrEmpty()) {
            return ""
        }
        return StringBuffer().append(before.takeLastWhile {
            it != ' '
        }).toString()
    }

    fun getAfterCursorInSurroundingWord(): String {
        val after = ic.getTextAfterCursor(Constants.MAX_INPUT_LEN, 0)
        if (after.isNullOrEmpty()) {
            return ""
        }
        return StringBuffer()
                .append(after.takeWhile {
                    it != ' '
                }).toString()
    }

    fun isLastWord(): Boolean {
        val se = ic.getTextAfterCursor(Constants.MAX_INPUT_LEN, 0) ?: return true
        return se.isEmpty()
    }

    fun deleteCurrentSelection() {
        if (selectionEnd > selectionStart) {
            ic.setComposingRegion(selectionStart, selectionEnd)
            val newCursorPosition = if (selectionEndOld > 1) {
                selectionStartOld - 1
            } else {
                0
            }
            ic.setComposingText("", newCursorPosition)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
        ic.setComposingText("", 1)
    }

    /**
     * Called (in the InputMethodService) to update the selection indexes
     */
    fun updateSelection(start: Int, end: Int, startOld: Int, endOld:Int) {
        selectionStart = start
        selectionStartOld = startOld
        selectionEnd = end
        selectionEndOld = endOld
    }
}
