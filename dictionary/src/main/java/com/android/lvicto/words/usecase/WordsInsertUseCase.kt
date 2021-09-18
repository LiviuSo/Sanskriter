package com.android.lvicto.words.usecase

import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class WordsInsertUseCase(private val wordsDao: WordDao) {

    sealed class Result {
        object Success: Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun insertWord(word: Word): Result = withContext(Dispatchers.IO) {
        try {
            wordsDao.insert(word)
            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }

    suspend fun insertWords(words: List<Word>): Result = withContext(Dispatchers.IO) {
        try {
            wordsDao.insert(words)
            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }
}
