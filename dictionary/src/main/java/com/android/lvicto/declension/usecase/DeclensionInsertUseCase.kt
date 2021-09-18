package com.android.lvicto.declension.usecase

import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.entity.Declension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeclensionInsertUseCase(private val declensionDao: DeclensionDao) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String) : Result()
    }

    suspend fun insert(declension: Declension) = withContext(Dispatchers.IO) {
        try {
            declensionDao.insert(declension)
            Result.Success
        } catch (e: Exception) {
            Result.Failure("Unable to insert declension")
        }
    }

    suspend fun insert(declensions: List<Declension>) = withContext(Dispatchers.IO) {
        try {
            declensionDao.insert(declensions)
            Result.Success
        } catch (e: Exception) {
            Result.Failure("Unable to insert declension")
        }
    }
}