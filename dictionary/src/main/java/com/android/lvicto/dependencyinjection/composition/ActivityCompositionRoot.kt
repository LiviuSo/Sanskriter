package com.android.lvicto.dependencyinjection.composition

import android.view.LayoutInflater
import com.android.lvicto.common.ImportPickerCode
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.common.factory.ViewMvcFactory
import com.android.lvicto.conjugation.usecase.ConjugationAddUseCase
import com.android.lvicto.conjugation.usecase.ConjugationFetchUseCase
import com.android.lvicto.conjugation.usecase.ConjugationImportExportUseCase
import com.android.lvicto.db.Converters
import com.android.lvicto.db.dao.ConjugationDao
import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.declension.usecase.*
import com.android.lvicto.common.factory.ControllerMvcFactory
import com.android.lvicto.db.dao.gtypes.*
import com.android.lvicto.words.usecase.*
import com.google.gson.Gson

class ActivityCompositionRoot(
    val activity: BaseActivity,
    private val appComponent: AppCompositionRoot
) {

    private val wordDao: WordDao get() = appComponent.wordDao
    private val numeralDao: NumeralDao get() = appComponent.numeralDao
    private val otherDao: OtherDao get() = appComponent.otherDao
    private val pronounDao: PronounDao get() = appComponent.pronounDao
    private val substantiveDao: SubstantiveDao get() = appComponent.substantiveDao
    private val verbDao: VerbDao get() = appComponent.verbDao
    private val conjugationDao: ConjugationDao get() = appComponent.conjugationDao
    private val declensionDao: DeclensionDao get() = appComponent.declensionDao

    private val layoutInflator: LayoutInflater get() = activity.layoutInflater

    private val gson: Gson get() = appComponent.gson

    val converters: Converters get() = appComponent.converters

    val controllerMvcFactory: ControllerMvcFactory get() = ControllerMvcFactory(activity)

    val dialogManager: DialogManager get() = DialogManager(activity, activity.supportFragmentManager)

    val application = appComponent.application

    val viewMvcFactory: ViewMvcFactory get() = ViewMvcFactory(layoutInflator, dialogManager)

    val conjugationAddUseCase: ConjugationAddUseCase get() = ConjugationAddUseCase(conjugationDao)
    val conjugationFetchUseCase: ConjugationFetchUseCase get() = ConjugationFetchUseCase(conjugationDao)
    val conjugationImportExportUseCase: ConjugationImportExportUseCase get() = ConjugationImportExportUseCase(activity)

    val wordsFetchUseCase: WordsFetchUseCase get() = WordsFetchUseCase(wordDao, substantiveDao, pronounDao, verbDao, numeralDao, otherDao)
    val wordsDeleteUseCase: WordsDeleteUseCase get() = WordsDeleteUseCase(wordDao, substantiveDao, pronounDao, verbDao, numeralDao, otherDao)
    val wordsWordsInsertUseCase: WordsInsertUseCase get() = WordsInsertUseCase(wordDao, substantiveDao, pronounDao, verbDao, numeralDao, otherDao)
    val wordsWordsUpdateUseCase: WordsUpdateUseCase get() = WordsUpdateUseCase(wordDao, substantiveDao, pronounDao, verbDao, numeralDao, otherDao)
    val wordsReadFromFileUseCase: WordsReadFromFileUseCase get() = WordsReadFromFileUseCase(activity)
    val wordsWriteToFileUseCase: WordsWriteToFileUseCase get() = WordsWriteToFileUseCase(activity, gson)
    val wordsFilterUseCase: WordsFilterUseCase get() = WordsFilterUseCase(wordDao, substantiveDao, pronounDao, verbDao, numeralDao, otherDao)

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

    val importPickerCode: ImportPickerCode = appComponent.importCodeHolder
}
