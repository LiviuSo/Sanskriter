package com.android.lvicto.words.view

import com.android.lvicto.db.entity.Declension
import com.android.lvicto.declension.adapter.DeclensionAdapter

class WordDetailsViewImpl(val adapter: DeclensionAdapter? = null) : WordDetailsView {
    override fun setDeclensions(declensions: List<Declension>) {
        adapter?.refresh(declensions) // todo remove when second recView in place
    }
}