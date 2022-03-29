package com.android.lvicto.declension.usecase

import android.content.Context
import android.net.Uri
import com.android.lvicto.common.readData
import com.android.lvicto.db.data.Declensions
import com.android.lvicto.db.entity.Declension
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeclensionsReadFromFileUseCase(val context: Context, val gson: Gson) {

    sealed class Result {
        class Success(val declensions: List<Declension>): Result()
        class Failure(val message: String): Result()
    }

    suspend fun loadDeclensionsFromFile(uri: Uri): Result = withContext(Dispatchers.IO) {
        try {
            val json = context.readData(uri = uri)
            val declensions = gson.fromJson(json, Declensions::class.java)
            Result.Success(declensions.declensions)
        } catch (e: Exception) {
            Result.Failure("Unable to fetch declensions from file")
        }
    }

}