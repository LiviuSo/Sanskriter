package com.android.lvicto.declension.usecase

import android.content.Context
import com.android.lvicto.common.extention.writeDataToFile
import com.android.lvicto.db.data.Declensions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeclensionWriteToFileUseCase(val context: Context, val gson: Gson) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String) : Result()
    }

    suspend fun writeDataToFile(declensions: Declensions, fileName: String): Result =
        withContext(Dispatchers.IO) {
            try {
                context.writeDataToFile(gson.toJson(declensions), fileName)
                Result.Success
            } catch (e: Exception) {
                Result.Success
            }
        }
}