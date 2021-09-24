package com.android.lvicto.dependencyinjection.composition

import android.view.LayoutInflater
import com.android.lvicto.common.ImportPickerCodeHolder
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.dialog.new.DialogManager2
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.common.view.factory.ViewMvcFactory
import com.android.lvicto.conjugation.usecase.ConjugationAddUseCase
import com.android.lvicto.conjugation.usecase.ConjugationFetchUseCase
import com.android.lvicto.conjugation.usecase.ConjugationImportExportUseCase
import com.android.lvicto.db.dao.ConjugationDao
import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.declension.usecase.*
import com.android.lvicto.words.controller.ControllerMvcFactory
import com.android.lvicto.words.usecase.*
import com.google.gson.Gson

class ActivityCompositionRoot(
    val activity: BaseActivity,
    private val appComponent: AppCompositionRoot
) {

    val controllerMvcFactory: ControllerMvcFactory get() = ControllerMvcFactory(activity)

    val dialogManager2: DialogManager2 get() = DialogManager2(activity, activity.supportFragmentManager)

    private val layoutInflator: LayoutInflater get() = activity.layoutInflater

    private val conjugationDao: ConjugationDao get() = appComponent.conjugationDao

    private val wordsDao: WordDao get() = appComponent.wordDao

    private val declensionDao: DeclensionDao get() = appComponent.declensionDao

    private val gson: Gson get() = appComponent.gson

    val application = appComponent.application

    val dialogManager: DialogManager get() = DialogManager(activity = activity)

    val viewMvcFactory: ViewMvcFactory get() = ViewMvcFactory(layoutInflator, dialogManager, dialogManager2)

    val conjugationAddUseCase: ConjugationAddUseCase get() = ConjugationAddUseCase(conjugationDao)

    val conjugationFetchUseCase: ConjugationFetchUseCase get() = ConjugationFetchUseCase(conjugationDao)

    val conjugationImportExportUseCase: ConjugationImportExportUseCase get() = ConjugationImportExportUseCase(activity)

    val wordsFetchUseCase: WordsFetchUseCase get() = WordsFetchUseCase(wordsDao)

    val wordsDeleteUseCase: WordsDeleteUseCase get() = WordsDeleteUseCase(wordsDao)

    val wordsWordsInsertUseCase: WordsInsertUseCase get() = WordsInsertUseCase(wordsDao)

    val wordsWordsUpdateUseCase: WordsUpdateUseCase get() = WordsUpdateUseCase(wordsDao)

    val wordsReadFromFileUseCase: WordsReadFromFileUseCase get() = WordsReadFromFileUseCase(activity)

    val wordsWriteToFileUseCase: WordsWriteToFileUseCase get() = WordsWriteToFileUseCase(activity, gson)

    val wordsFilterUseCase: WordsFilterUseCase get() = WordsFilterUseCase(wordsDao)

    val declensionDeleteUseCase: DeclensionDeleteUseCase get() = DeclensionDeleteUseCase(declensionDao = declensionDao)

    val declensionFetchUseCase: DeclensionFetchUseCase get() = DeclensionFetchUseCase(declensionDao)

    val declensionFilterUseCase: DeclensionFilterUseCase get() = DeclensionFilterUseCase(declensionDao)

    val declensionInsertUseCase: DeclensionInsertUseCase get() = DeclensionInsertUseCase(declensionDao)

    val declensionReadUseCase: DeclensionsReadFromFileUseCase get() = DeclensionsReadFromFileUseCase(activity, gson)

    val declensionWriteUseCase: DeclensionWriteToFileUseCase get() = DeclensionWriteToFileUseCase(activity, gson)

    val resultLauncherManager: ResultLauncherManager by lazy {
        ResultLauncherManager(activity)
    }

    val eventBus: ResultEventBus get() = appComponent.eventBus

    val importPickerCodeHolder: ImportPickerCodeHolder = appComponent.importCodeHolder
}
