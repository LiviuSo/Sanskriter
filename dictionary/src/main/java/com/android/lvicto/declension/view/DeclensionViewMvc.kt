package com.android.lvicto.declension.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.db.entity.Declension

interface DeclensionsViewMvc {
    interface Listener {
        fun onDeleteDeclension(declension: Declension)
        fun onFetchAll()
        fun onSaveDeclensions(declension: Declension)
        fun onButtonImport()
        fun onButtonExport()
        fun onFilter()
    }

    fun setUpUI(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        @LayoutRes layoutId: Int
    ): View

    fun setDeclensions(declensions: List<Declension>)

    // region private
    fun requireActivity(): BaseActivity
}