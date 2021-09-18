package com.android.lvicto.words.usecase

import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class WordsUpdateUseCase(val wordDao: WordDao) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun updateWord(word: Word) = withContext(Dispatchers.IO) {
        try {
            wordDao.update(word)
            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }
}