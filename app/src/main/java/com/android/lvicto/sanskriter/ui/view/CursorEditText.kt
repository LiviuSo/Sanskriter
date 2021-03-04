package com.android.lvicto.sanskriter.ui.view

import android.content.Context
import android.util.AttributeSet

class CursorEditText(context: Context, attr: AttributeSet) : androidx.appcompat.widget.AppCompatEditText(context, attr) {

    private var cursorWatcher: CursorWatcher? = null

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        cursorWatcher?.onSelectionChanged(text.toString(), selStart, selEnd)
    }

    fun addCursorWatcher(watcher: CursorWatcher) {
        cursorWatcher = watcher
    }
}