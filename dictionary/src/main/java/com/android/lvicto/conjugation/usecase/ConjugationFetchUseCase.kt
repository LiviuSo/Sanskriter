package com.android.lvicto.conjugation.usecase

import android.util.Log
import com.android.lvicto.common.constants.Constants.NONE
import com.android.lvicto.db.dao.ConjugationDao
import com.android.lvicto.db.entity.Conjugation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConjugationFetchUseCase(private val conjugationDao: ConjugationDao) { // todo refactor not to pass Context

    val LOG = "debconj"

    sealed class Result {
        class Success(val conjugations: List<Conjugation>) : Result()
        class Failure(val message: String) : Result()
    }

    // todo extract private utility createFilter()
    suspend fun filter(conjugation: Conjugation? = null): Result = withContext(Dispatchers.IO) {
        try {
            val list = if (conjugation != null) {
                val paradigmRoot = if (conjugation.paradigmRoot.isEmpty()
                    || conjugation.paradigmRoot == NONE) {
                    "%"
                } else {
                    "%${conjugation.paradigmRoot}%"
                }

                val ending = if (conjugation.ending.isEmpty()
                    || conjugation.ending == NONE) {
                    "%"
                } else {
                    "%${conjugation.ending}"
                }

                val verbClass = if (conjugation.verbClass == null
                    || conjugation.verbClass?.isEmpty() == true
                    || conjugation.verbClass?.equals(NONE) == true) {
                    "%" // maybe
                } else {
                    "${conjugation.verbClass}"
                }

                val verbNumber = if (conjugation.verbNumber == null
                    || conjugation.verbNumber?.isEmpty() == true
                    || conjugation.verbNumber?.equals(NONE) == true) {
                    "%"
                } else {
                    "${conjugation.verbNumber}"
                }

                val verbPerson = if (conjugation.verbPerson == null
                    || conjugation.verbPerson?.isEmpty() == true
                    || conjugation.verbPerson?.equals(NONE) == true) {
                    "%"
                } else {
                    "${conjugation.verbPerson}"
                }

                val verbMode = if (conjugation.verbMode == null
                    || conjugation.verbMode?.isEmpty() == true
                    || conjugation.verbMode?.equals(NONE) == true) {
                    "%"
                } else {
                    "${conjugation.verbMode}"
                }

                val verbTime = if (conjugation.verbTime == null
                    || conjugation.verbTime?.isEmpty() == true
                    || conjugation.verbTime?.equals(NONE) == true) {
                    "%"
                } else {
                    "${conjugation.verbTime}"
                }

                val verbParadygmType = if (conjugation.verbParadygmType == null
                    || conjugation.verbParadygmType?.isEmpty() == true
                    || conjugation.verbParadygmType?.equals(NONE) == true) {
                    "%"
                } else {
                    "${conjugation.verbParadygmType}"
                }

                Log.d(LOG,
                    """query params: ending=$ending, verbClass=$verbClass, verbNumber=$verbNumber,
                        | verbPerson=$verbPerson, 
                        | verbTime=$verbTime, verbMode=$verbMode,
                        | verbParadygmType=$verbParadygmType""".trimMargin())
                conjugationDao.getConjugations(paradigmRoot,
                    ending,
                    verbClass,
                    verbNumber,
                    verbPerson,
                    verbTime,
                    verbMode,
                    verbParadygmType)
            } else {
                conjugationDao.getAll()
            }
            Log.d(LOG, "getConjugations")
            Result.Success(list)
        } catch (t: Exception) {
            if(t is CancellationException) {
                Log.d(LOG, "getConjugations CancellationException")
                Result.Failure("filter($conjugation) failed because ${t.message}")
            } else {
                Log.d(LOG, "getConjugations exception $t")
                throw t
            }
        }
    }

    suspend fun filterByEnding(ending: String): Result {
        val conjugation = Conjugation(0,
            "",
            ending,
            null,
            null,
            null,
            null,
            null,
            null)
        return filter(conjugation)
    }

}