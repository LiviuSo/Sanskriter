package com.android.lvicto.dependencyinjection

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
import com.android.lvicto.db.Converters
import com.android.lvicto.declension.usecase.*
import com.android.lvicto.dependencyinjection.composition.ControllerCompositionRoot
import com.android.lvicto.words.controller.ControllerMvcFactory
import com.android.lvicto.words.usecase.*
import java.lang.reflect.Field

class Injector(private val compositionRoot: ControllerCompositionRoot) {

    fun inject(client: Any) {
        for (field in getAllFields(client)) {
            if (isAnnotatedForInjection(field)) {
                injectField(client, field)
            }
        }
    }

    private fun getAllFields(client: Any): Array<out Field> {
        val clientClass = client::class.java
        return clientClass.declaredFields
    }

    private fun isAnnotatedForInjection(field: Field): Boolean {
        val fieldAnnotations = field.annotations
        for (annotation in fieldAnnotations) {
            if (annotation is Service) {
                return true
            }
        }
        return false
    }

    private fun injectField(client: Any, field: Field) {
        val isAccessibleInitially = field.isAccessible
        field.isAccessible = true
        field.set(client, getServiceForClass(field.type))
        field.isAccessible = isAccessibleInitially
    }

    private fun getServiceForClass(type: Class<*>): Any = when (type) {
        BaseActivity::class.java -> { compositionRoot.activity }
        ViewMvcFactory::class.java -> { compositionRoot.viewMvcFactory }
        DialogManager::class.java -> { compositionRoot.dialogManager }
        ResultLauncherManager::class.java -> { compositionRoot.resultLauncherManager }
        ResultEventBus::class.java -> { compositionRoot.eventBus }
        ImportPickerCodeHolder::class.java -> { compositionRoot.importPickerCodeHolder }
        ConjugationAddUseCase::class.java -> { compositionRoot.conjugationAddUseCase }
        ConjugationFetchUseCase::class.java -> { compositionRoot.conjugationFetchUseCase }
        ConjugationImportExportUseCase::class.java -> { compositionRoot.conjugationImportExportUseCase }
        WordsFetchUseCase::class.java -> { compositionRoot.wordsFetchUseCase }
        WordsDeleteUseCase::class.java -> { compositionRoot.wordsDeleteUseCase }
        WordsInsertUseCase::class.java -> { compositionRoot.wordsInsertWordUseCase }
        WordsUpdateUseCase::class.java -> { compositionRoot.wordsUpdateUseCase }
        WordsReadFromFileUseCase::class.java -> { compositionRoot.wordsReadFromFileUseCase }
        WordsWriteToFileUseCase::class.java -> { compositionRoot.wordsWriteToFileUseCase }
        WordsFilterUseCase::class.java -> { compositionRoot.wordsFilterUseCase }
        DeclensionDeleteUseCase::class.java -> { compositionRoot.declensionDeleteUseCase }
        DeclensionFetchUseCase::class.java -> { compositionRoot.declensionFetchUseCase }
        DeclensionFilterUseCase::class.java -> { compositionRoot.declensionFilterUseCase }
        DeclensionInsertUseCase::class.java -> { compositionRoot.declensionInsertUseCase }
        DeclensionsReadFromFileUseCase::class.java -> { compositionRoot.declensionReadUseCase }
        DeclensionWriteToFileUseCase::class.java -> { compositionRoot.declensionWriteUseCase }
        DialogManager2::class.java -> { compositionRoot.dialogManager2 }
        ControllerMvcFactory::class.java -> { compositionRoot.controllerMvcFactory }
        Converters::class.java -> { compositionRoot.converters }
        else -> { throw Exception("unsupported service type: $type") }
    }

}