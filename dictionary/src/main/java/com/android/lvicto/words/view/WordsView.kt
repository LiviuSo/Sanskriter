package com.android.lvicto.words.view

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.ObservableMvc

interface WordsView : ObservableMvc<WordsView.Listener> {
    interface Listener {
        fun onFilterEnIAST(filterEn: String, filterIast: String)
        fun onInitWords()
        fun onDeleteWords(wordsToRemove: List<Word>)
        fun onImport()
        fun onExport()
        fun onReadFromFile(uri: Uri?)
    }

    fun onFilePicked(data: Intent?)
    fun setWords(words: List<Word>?)
    fun isSearchVisible(): Boolean
    fun getSearchEnString(): String
    fun getSearchIASTString(): String
    fun setResultLauncher(resultLauncher: ActivityResultLauncher<Intent>)
    fun unselectSelectedToRemove()
    fun showProgress()
    fun hideProgress()
    fun setupSearchFromOutside(searchIast: String?, searchEn: String?)
}