package com.android.lvicto.words.usecase

import android.content.Context
import com.android.lvicto.common.extention.writeDataToFile
import com.android.lvicto.db.data.Words
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class WordsWriteToFileUseCase(val context: Context, val gson: Gson) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun writeWordsToFile(words: Words, filename: String): Result {
        return withContext(Dispatchers.IO) {
            try {
                gson.toJson(words)?.let {
                    context.writeDataToFile(it, filename)
                    Result.Success
                } ?: run {
                    Result.Failure("Unable to parse or write to file")
                }
            } catch (e: Exception) {
                Result.Failure(e.message)
            }
        }
    }
}