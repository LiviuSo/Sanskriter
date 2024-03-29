package com.android.lvicto.conjugation.view

import com.android.lvicto.common.base.ObservableMvc
import com.android.lvicto.db.entity.Conjugation

interface ConjugationViewMvc : ObservableMvc<ConjugationViewMvc.Listener> {
    interface Listener {
        fun onConjugationAddAction(conjugation: Conjugation?)
        fun onConjugationDeleteAction(conjugation: Conjugation?)
        fun onConjugationFilterAction(conjugation: Conjugation?)
        fun onConjugationDetectAction(form: String)
        fun onConjugationImport()
        fun onConjugationExport()
    }

    fun setConjugations(conjugations: List<Conjugation>)
    fun showProgress()
    fun hideProgress()
    fun setFormRoot(formRoot: String)
}