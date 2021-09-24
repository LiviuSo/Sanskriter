package com.android.lvicto.dependencyinjection.composition

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
import com.android.lvicto.declension.usecase.*
import com.android.lvicto.words.controller.ControllerMvcFactory
import com.android.lvicto.words.usecase.*

class ControllerCompositionRoot(private val activityCompositionRoot: ActivityCompositionRoot) {

    val wordsFetchUseCase: WordsFetchUseCase get() = activityCompositionRoot.wordsFetchUseCase

    val wordsDeleteUseCase: WordsDeleteUseCase get() = activityCompositionRoot.wordsDeleteUseCase

    val wordsInsertWordUseCase: WordsInsertUseCase get() = activityCompositionRoot.wordsWordsInsertUseCase

    val wordsUpdateUseCase: WordsUpdateUseCase get() = activityCompositionRoot.wordsWordsUpdateUseCase

    val wordsReadFromFileUseCase: WordsReadFromFileUseCase get() = activityCompositionRoot.wordsReadFromFileUseCase

    val wordsWriteToFileUseCase: WordsWriteToFileUseCase get() = activityCompositionRoot.wordsWriteToFileUseCase

    val wordsFilterUseCase: WordsFilterUseCase get() = activityCompositionRoot.wordsFilterUseCase

    val conjugationAddUseCase: ConjugationAddUseCase get() = activityCompositionRoot.conjugationAddUseCase

    val conjugationFetchUseCase: ConjugationFetchUseCase get() = activityCompositionRoot.conjugationFetchUseCase

    val conjugationImportExportUseCase: ConjugationImportExportUseCase get() = activityCompositionRoot.conjugationImportExportUseCase

    val declensionDeleteUseCase: DeclensionDeleteUseCase get() = activityCompositionRoot.declensionDeleteUseCase

    val declensionFetchUseCase: DeclensionFetchUseCase get() = activityCompositionRoot.declensionFetchUseCase

    val declensionFilterUseCase: DeclensionFilterUseCase get() = activityCompositionRoot.declensionFilterUseCase

    val declensionInsertUseCase: DeclensionInsertUseCase get() = activityCompositionRoot.declensionInsertUseCase

    val declensionReadUseCase: DeclensionsReadFromFileUseCase get() = activityCompositionRoot.declensionReadUseCase

    val declensionWriteUseCase: DeclensionWriteToFileUseCase get() = activityCompositionRoot.declensionWriteUseCase

    val dialogManager: DialogManager get() = activityCompositionRoot.dialogManager

    val viewMvcFactory: ViewMvcFactory get() = activityCompositionRoot.viewMvcFactory

    val resultLauncherManager: ResultLauncherManager get() = activityCompositionRoot.resultLauncherManager

    val eventBus: ResultEventBus get() = activityCompositionRoot.eventBus

    val activity: BaseActivity get() = activityCompositionRoot.activity

    val importPickerCodeHolder: ImportPickerCodeHolder = activityCompositionRoot.importPickerCodeHolder

    val dialogManager2: DialogManager2 = activityCompositionRoot.dialogManager2

    val controllerMvcFactory: ControllerMvcFactory = activityCompositionRoot.controllerMvcFactory
}