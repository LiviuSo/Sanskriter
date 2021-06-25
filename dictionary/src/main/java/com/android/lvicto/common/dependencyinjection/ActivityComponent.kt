package com.android.lvicto.common.dependencyinjection

import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.db.dao.ConjugationDao
import com.android.lvicto.conjugation.usecases.ConjugationAddUseCase
import com.android.lvicto.conjugation.usecases.ConjugationFetchUseCase
import com.android.lvicto.conjugation.usecases.ConjugationImportExportUseCase
import com.android.lvicto.conjugation.view.ConjugationViewMvc

class ActivityComponent(
    val activity: AppCompatActivity,
    private val appComponent: AppCompositionRoot
) {

    val application = appComponent.application

    private val conjugationDao: ConjugationDao
        get() = appComponent.conjugationDao

    val conjugationAddUseCase: ConjugationAddUseCase
        get() = ConjugationAddUseCase(conjugationDao)

    val conjugationFetchUseCase: ConjugationFetchUseCase
        get() = ConjugationFetchUseCase(
            conjugationDao
        )

    val conjugationImportExportUseCase: ConjugationImportExportUseCase
        get() = ConjugationImportExportUseCase(
            activity
        )

    val viewMvc: ConjugationViewMvc
        get() = ConjugationViewMvc(activity = activity)


    // todo complete

}
