package com.android.lvicto.conjugation.data

import android.content.Context
import android.net.Uri
import com.android.lvicto.common.db.data.Conjugations
import com.android.lvicto.common.db.entity.Conjugation
import com.android.lvicto.common.util.readData
import com.android.lvicto.common.util.writeDataToFile
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConjugationImportExportUseCase(val context: Context) {

    sealed class Result {
        class SuccessRead(val conjugations: List<Conjugation>?) : ConjugationImportExportUseCase.Result()
        object SuccessWrite : ConjugationImportExportUseCase.Result()
        object Failure : Result()
    }

    suspend fun importConjugations(uri: Uri): Result =
        withContext(Dispatchers.IO) {
            try {
                val json = context.readData(uri = uri)
                val conjugations = Gson().fromJson(json, Conjugations::class.java)

                Result.SuccessRead(conjugations.conjunctions)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Result.Failure
                } else {
                    throw e
                }
            }
        }

    suspend fun exportConjugations(conjugations: List<Conjugation>, fileName: String): Result =
        withContext(Dispatchers.IO) {
            try {
                if (context.writeDataToFile(Gson().toJson(Conjugations(conjugations)), fileName)) {
                    Result.SuccessWrite
                } else {
                    Result.Failure
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Result.Failure
                } else {
                    throw e
                }
            }
        }

}