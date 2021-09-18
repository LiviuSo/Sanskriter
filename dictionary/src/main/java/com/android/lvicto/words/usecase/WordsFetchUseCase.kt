package com.android.lvicto.words.usecase

import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.entity.Word
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordsFetchUseCase(private val wordsDao: WordDao) {

    sealed class Result {
        class Success(val words: List<Word>) : Result()
        class Failure(val message: String) : Result()
    }

    suspend fun fetchWords(): Result = withContext(Dispatchers.IO) {
        try {
            Result.Success(wordsDao.getAllWords())
        } catch (e: CancellationException) {
            Result.Failure("Cancellation exception")
        } catch (e: Exception) {
            Result.Failure("Unexpected error: ${e.message} ")
        }
    }
}