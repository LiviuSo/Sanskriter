package com.android.lvicto.words.view

import android.view.View
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.ObservableMvc
import com.android.lvicto.db.entity.Declension

interface WordDetailsView : ObservableMvc<WordDetailsView.Listener> {

    interface Listener {
        fun onDetectDeclension(declension: String)
        fun onAddWord(v: View, newWord: Word)
        fun onEditWord(oldWord: Word?, newWord: Word)
        fun onAddWordError()
    }

    fun setDeclensions(declensions: List<Declension>)

    fun isParadigmImplemented(word: Word?): Boolean

    fun modifyWord(newWord: Word)

    fun getOldWord(): Word
}
