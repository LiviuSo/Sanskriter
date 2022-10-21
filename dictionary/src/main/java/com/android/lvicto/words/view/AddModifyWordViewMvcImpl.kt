package com.android.lvicto.words.view

import com.android.lvicto.db.entity.Declension
import com.android.lvicto.declension.adapter.DeclensionAdapter

class AddModifyWordViewMvcImpl(val adapter: DeclensionAdapter? = null) : AddModifyWordViewMvc {
    override fun setDeclensions(declensions: List<Declension>) {
        adapter?.refresh(declensions) // todo remove when second recView in place
    }
}