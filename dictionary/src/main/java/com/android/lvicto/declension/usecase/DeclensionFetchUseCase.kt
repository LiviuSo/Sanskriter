package com.android.lvicto.declension.usecase

import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.entity.Declension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeclensionFetchUseCase(private val declensionDao: DeclensionDao) {

    sealed class Result {
        class Success(val declensions: List<Declension>) : Result()
        class SuccessSuffixes(val declensions: List<String>) : Result()
        class Failure(val message: String) : Result()
    }

    suspend fun getAll(): Result = withContext(Dispatchers.IO) {
        try {
            val declensions = declensionDao.getAll()
            Result.Success(declensions)
        } catch (e: Exception) {
            Result.Failure("Unable to fetch declensions because: ${e.message}")
        }
    }

    suspend fun getSuffixes(part: String): Result = withContext(Dispatchers.IO) {
        try {
            val suffixes = declensionDao.getSuffixes(part)
            Result.SuccessSuffixes(suffixes)
        } catch (e: Exception) {
            Result.Failure("Unable to fetch suffixes because: ${e.message}")
        }
    }

}