package com.android.lvicto.common.dependencyinjection

import com.android.lvicto.conjugation.usecases.ConjugationAddUseCase
import com.android.lvicto.conjugation.usecases.ConjugationFetchUseCase
import com.android.lvicto.conjugation.usecases.ConjugationImportExportUseCase
import com.android.lvicto.conjugation.view.ConjugationViewMvc
import java.lang.reflect.Field

class Injector(private val compositionRoot: PresentationCompositionRoot) {

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
        ConjugationAddUseCase::class.java -> {
            compositionRoot.conjugationAddUseCase
        }
        ConjugationFetchUseCase::class.java -> {
            compositionRoot.conjugationFetchUseCase
        }
        ConjugationImportExportUseCase::class.java -> {
            compositionRoot.conjugationImportExportUseCase
        }
        ConjugationViewMvc::class.java -> {
            compositionRoot.viewMvc
        }
        else -> {
            throw Exception("unsupported service type: $type")
        }
    }

}