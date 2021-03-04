package com.android.lvicto.sanskriter.ui.view

interface CursorWatcher {
    fun onSelectionChanged(string: String, begin: Int, end: Int)
}