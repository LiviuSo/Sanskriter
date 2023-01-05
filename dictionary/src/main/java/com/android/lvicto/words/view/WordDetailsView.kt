package com.android.lvicto.words.view

import android.view.View
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.ObservableMvc

interface WordDetailsView : ObservableMvc<WordDetailsView.Listener> {

    interface Listener {
        fun onShowWordGrammar(word: Word?)
        fun onAddWord(view: View, word: Word)
        fun onEditWord(oldWord: Word?, newWord: Word)
        fun onAddWordError()
    }

    fun modifyWord(newWord: Word)
    fun buildUI()
}
