package com.android.lvicto.words.usecase

import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordsFilterUseCase(val wordDao: WordDao) {

    sealed class Result {
        class Success(val words: List<Word>) : Result()
        class Failure(val message: String) : Result()
    }

    suspend fun filter(key: String, isPrefix: Boolean): Result = withContext(Dispatchers.IO) {
        try {
            val filter = if (isPrefix) {
                "$key%"
            } else {
                "%$key%"
            }
            val words = wordDao.getWords(filter)
            Result.Success(words)
        } catch (e: Exception) {
            Result.Failure("Unable to filter")
        }
    }

    suspend fun filter(filterPrefix: String, filter: String): Result = withContext(Dispatchers.IO) {
        try {
            val filter1 = "%$filter%"
            val filter2 = "$filterPrefix%" // use prefix
            val words = wordDao.getWords(filter1, filter2)
            Result.Success(words)
        } catch (e: Exception) {
            Result.Failure("")
        }
    }

    suspend fun filter(root: String, paradigm: String, isPrefix: Boolean): Result {
        return withContext(Dispatchers.IO) {
            try {
                val filter = if (isPrefix) {
                    "$root%"
                } else {
                    "%$root%"
                }
                val words = wordDao.getNounsAndAdjectives(filter, paradigm)
                Result.Success(words)
            } catch (e: java.lang.Exception) {
                Result.Failure("Unable to filter by paradigm")
            }
        }
    }
}