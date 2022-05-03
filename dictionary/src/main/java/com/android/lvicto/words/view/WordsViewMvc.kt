package com.android.lvicto.words.view

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.android.lvicto.common.WordWrapper
import com.android.lvicto.common.view.viewinterface.ObservableMvc

interface WordsViewMvc : ObservableMvc<WordsViewMvc.WordsViewListener> {
    interface WordsViewListener {
        fun onFilterIASTEn(searchIAST: String?, searchEn: String?)
        fun onFilterEnIAST(filterEn: String, filterIast: String)
        fun onInitWords()
        fun onDeleteWords(wordsToRemove: List<WordWrapper>)
        fun onImport()
        fun onExport()
        fun onReadFromFile(uri: Uri?)
    }

    fun onFilePicked(data: Intent?)
    fun setWords(words: List<WordWrapper>?)
    fun isSearchVisible(): Boolean
    fun getSearchEnString(): String
    fun getSearchIASTString(): String
    fun setResultLauncher(resultLauncher: ActivityResultLauncher<Intent>)
    fun unselectSelectedToRemove()
    fun showProgress()
    fun hideProgress()
    fun setupSearchFromOutside(searchIast: String?, searchEn: String?)
    fun getRootView(): View
}