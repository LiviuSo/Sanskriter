package com.android.lvicto.conjugation.usecase

import android.util.Log
import com.android.lvicto.db.dao.ConjugationDao
import com.android.lvicto.db.entity.Conjugation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConjugationAddUseCase(private val conjugationDao: ConjugationDao) { // todo refactor not to pass Context

    val LOG = "debconj"

    sealed class Result {
        object Success : Result()
        object Failure : Result()
    }

    suspend fun addConjugation(conjugation: Conjugation): Result = withContext(Dispatchers.IO) {
        try {
            conjugationDao.insert(conjugation)
            Log.d(LOG, "addConjugation: $conjugation")
            Result.Success
        } catch (e: Exception) {
            if (e is CancellationException) {
                Log.d(LOG, "addConjugation CancellationException")
                Result.Failure
            } else {
                Log.d(LOG, "exception $e")
                throw  e
            }
        }
    }

    suspend fun delete(conjugations: List<Conjugation>): Result = withContext(Dispatchers.IO) {
        try {
            conjugationDao.delete(conjugations)
            Log.d(LOG, "delete($conjugations)")
            Result.Success
        } catch (e: java.lang.Exception) {
            if (e is CancellationException) {
                Result.Failure
            } else {
                throw e
            }
        }
    }

    suspend fun addConjugations(conjugations: List<Conjugation>) = withContext(Dispatchers.IO) {
        try {
            conjugationDao.insert(conjugations)
            Log.d(LOG, "addConjugations: $conjugations")
        } catch (e: Exception) {
            if (e is CancellationException) {
                Result.Failure
            } else {
                throw e
            }
        }
    }

}