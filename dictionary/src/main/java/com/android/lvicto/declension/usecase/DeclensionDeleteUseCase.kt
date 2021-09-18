package com.android.lvicto.declension.usecase

import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.entity.Declension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class DeclensionDeleteUseCase(private val declensionDao: DeclensionDao) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String) : Result()
    }

    suspend fun delete(declension: Declension) = withContext(Dispatchers.IO) {
        try {
            declensionDao.deleteDeclension(arrayListOf(declension))
            Result.Success
        } catch (e: Exception) {
            Result.Failure("Unable to delete declension $declension")
        }
    }
}