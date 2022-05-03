package com.android.lvicto.words.usecase

import android.content.Context
import android.net.Uri
import com.android.lvicto.common.WordWrapper
import com.android.lvicto.common.readData
import com.android.lvicto.db.data.Words
import com.android.lvicto.db.data.WordsPlus
import com.android.lvicto.db.entity.Word
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class WordsReadFromFileUseCase(val context: Context) {

    sealed class Result {
        class Success(val words: List<Word>) : Result()
        class SuccessPlus(val words: List<WordWrapper>) : Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun readWords(uri: Uri): Result {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(readWordsFromFile(uri))
            } catch (e: Exception) {
                Result.Failure(e.message)
            }
        }
    }

    private suspend fun readWordsFromFile(uri: Uri): List<Word> = coroutineScope {
        withContext(Dispatchers.IO) {
            val json = context.readData(uri = uri)
            val words = Gson().fromJson(json, Words::class.java)
            words.list
        }
    }

    suspend fun readWordsPlus(uri: Uri): Result {
        return withContext(Dispatchers.IO) {
            try {
                Result.SuccessPlus(readWordsFromFilePlus(uri))
            } catch (e: Exception) {
                Result.Failure(e.message)
            }
        }
    }

    private suspend fun readWordsFromFilePlus(uri: Uri): List<WordWrapper> = coroutineScope {
        withContext(Dispatchers.IO) {
            val json = context.readData(uri = uri)
            val words = Gson().fromJson(json, WordsPlus::class.java)
            words.list
        }
    }

}