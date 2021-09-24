package com.android.lvicto.words.view

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.android.lvicto.common.view.viewinterface.ObservableMvc
import com.android.lvicto.db.entity.Word

interface WordsViewMvc : ObservableMvc<WordsViewMvc.WordsViewListener> {
    interface WordsViewListener {
        fun onFilterIastEn(searchIast: String?, searchEn: String?)
        fun onFilterEnIast(filterEn: String, filterIast: String)
        fun onInitWords()
        fun onDeleteWords(wordsToRemove: List<Word>)
        fun onImport()
        fun onExport()
        fun onReadFromFile(uri: Uri?)
    }

    fun onActivityResult(requestCode: Int, data: Intent?)
    fun onFilePicked(data: Intent?)
    fun setWords(words: List<Word>?)
    fun isSearchVisible(): Boolean
    fun getSearchEnString(): String
    fun getSearchIastString(): String
    fun setResultLauncher(resultLauncher: ActivityResultLauncher<Intent>)
    fun unselectSelectedToRemove()
    fun showProgress()
    fun hideProgress()
    fun getRootView(): View
}