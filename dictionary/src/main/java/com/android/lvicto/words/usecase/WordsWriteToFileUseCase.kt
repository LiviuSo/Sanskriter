package com.android.lvicto.words.usecase

import android.content.Context
import com.android.lvicto.common.writeDataToFile
import com.android.lvicto.db.data.Words
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class WordsWriteToFileUseCase(val context: Context, val gson: Gson) {

    sealed class Result {
        class Success(val path: String) : Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun writeWordsToFile(words: Words, filename: String): Result = withContext(Dispatchers.IO) {
            try {
                gson.toJson(words)?.let {
                    with(context.writeDataToFile(it, filename)) {
                        if(this.isNotEmpty()) Result.Success(this)
                        else Result.Failure("Unable write to file.")
                    }
                } ?: run {
                    Result.Failure("Unable to parse.")
                }
            } catch (e: Exception) {
                Result.Failure(e.message)
            }
        }
}