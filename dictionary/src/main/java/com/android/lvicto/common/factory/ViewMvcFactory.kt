package com.android.lvicto.common.factory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.conjugation.view.ConjugationViewImpl
import com.android.lvicto.declension.view.DeclensionsViewImpl
import com.android.lvicto.words.view.WordGrammarViewImpl
import com.android.lvicto.words.view.WordDetailsViewImpl
import com.android.lvicto.words.view.WordsViewImpl

class ViewMvcFactory(val layoutInflater: LayoutInflater, private val dialogManager: DialogManager) {

    fun getConjugationView(activity: AppCompatActivity, parent: ViewGroup?): ConjugationViewImpl =
        ConjugationViewImpl(activity, layoutInflater, parent)

    fun getWordsViewMvc(
        activity: BaseActivity,
        container: ViewGroup?
    ): WordsViewImpl =
        WordsViewImpl(activity, layoutInflater, container, dialogManager)

    fun getDeclensionViewMvc(activity: BaseActivity): DeclensionsViewImpl =
        DeclensionsViewImpl(activity)

    fun getWordDetailsView(
        activity: BaseActivity,
        container: ViewGroup?,
        mode: Int,
        requestCode: Int,
        word: Word?
    ) = WordDetailsViewImpl(activity, word, mode, requestCode, container, layoutInflater)

    fun getWordGrammarView(activity: BaseActivity, container: ViewGroup?, word: Word) = WordGrammarViewImpl(activity, word, container, layoutInflater)
}