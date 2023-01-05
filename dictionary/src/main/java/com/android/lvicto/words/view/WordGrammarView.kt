package com.android.lvicto.words.view

import com.android.lvicto.common.Word
import com.android.lvicto.common.base.ObservableMvc
import com.android.lvicto.db.entity.Declension

interface WordGrammarView : ObservableMvc<WordGrammarView.Listener> {

    interface Listener {
        fun onDeclensionSearchKeyChanged(ending: String, word: Word)
    }

    fun setDeclensions(declensions: List<Declension>)
}