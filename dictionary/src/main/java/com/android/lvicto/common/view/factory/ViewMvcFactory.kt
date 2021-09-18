package com.android.lvicto.common.view.factory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.conjugation.view.ConjugationViewMvcImpl
import com.android.lvicto.declension.view.DeclensionsViewMvcImpl
import com.android.lvicto.words.view.WordsViewMvcImpl

class ViewMvcFactory(val layoutInflater: LayoutInflater, private val dialogManager: DialogManager) {

    fun getConjugationViewMvc(activity: AppCompatActivity, parent: ViewGroup?): ConjugationViewMvcImpl =
        ConjugationViewMvcImpl(activity, layoutInflater, parent)

    fun getWordsViewMvc(
        activity: BaseActivity,
        container: ViewGroup?
    ): WordsViewMvcImpl =
        WordsViewMvcImpl(activity, layoutInflater, container, dialogManager)

    fun getDeclensionViewMvc(activity: BaseActivity): DeclensionsViewMvcImpl =
        DeclensionsViewMvcImpl(activity)
}