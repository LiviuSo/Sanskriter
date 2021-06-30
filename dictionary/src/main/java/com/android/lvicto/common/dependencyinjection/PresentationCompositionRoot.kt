package com.android.lvicto.common.dependencyinjection

import com.android.lvicto.conjugation.usecases.ConjugationAddUseCase
import com.android.lvicto.conjugation.usecases.ConjugationFetchUseCase
import com.android.lvicto.conjugation.usecases.ConjugationImportExportUseCase
import com.android.lvicto.conjugation.view.ConjugationViewMvc
import com.android.lvicto.common.dialog.DialogManager

class PresentationCompositionRoot(private val activityCompositionRoot: ActivityComponent) {

    val conjugationAddUseCase: ConjugationAddUseCase get() = activityCompositionRoot.conjugationAddUseCase
    val conjugationFetchUseCase: ConjugationFetchUseCase get() = activityCompositionRoot.conjugationFetchUseCase
    val conjugationImportExportUseCase: ConjugationImportExportUseCase get() = activityCompositionRoot.conjugationImportExportUseCase
    val viewMvc: ConjugationViewMvc get() = activityCompositionRoot.viewMvc
    val dialogManager: DialogManager get() = activityCompositionRoot.dialogManager

}