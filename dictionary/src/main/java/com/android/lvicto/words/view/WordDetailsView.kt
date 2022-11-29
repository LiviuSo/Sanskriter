package com.android.lvicto.words.view

import com.android.lvicto.db.entity.Declension

interface WordDetailsView {
    fun setDeclensions(declensions: List<Declension>)
}
