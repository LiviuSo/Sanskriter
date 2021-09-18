package com.android.lvicto.words.usecase

import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class WordsDeleteUseCase(val wordDao: WordDao) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun deleteWords(words: List<Word>): Result = withContext(Dispatchers.IO) {
        try {
            wordDao.deleteWords(words)
            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }

}
