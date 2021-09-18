package com.android.lvicto.declension.usecase

import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.data.GrammaticalCase
import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.data.GrammaticalNumber
import com.android.lvicto.db.entity.Declension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class DeclensionFilterUseCase(private val declensionDao: DeclensionDao) {

    sealed class Result {
        class Success(val declensions: List<Declension>)
        class Failure(val message: String)
    }

    suspend fun filter(declension: Declension) = withContext(Dispatchers.IO) {
        try {
            val declensions = declensionDao.getDeclensions(
                if (declension.paradigm.isNotEmpty()) {
                    "%${declension.paradigm}%"
                } else {
                    "%%"
                }, if (declension.paradigmEnding.isNotEmpty()) {
                    "%${declension.paradigmEnding}"
                } else {
                    "%"
                }, if (declension.suffix.isNotEmpty()) {
                    "%${declension.suffix}%"
                } else {
                    "%"
                }, if (declension.gCase.abbr != GrammaticalCase.NONE.abbr) {
                    declension.gCase.abbr
                } else {
                    "%%"
                }, if (declension.gNumber.abbr != GrammaticalNumber.NONE.abbr) {
                    declension.gNumber.abbr
                } else {
                    "%%"
                }, if (declension.gGender.abbr != GrammaticalGender.NONE.abbr) {
                    declension.gGender.abbr
                } else {
                    "%%"
                }
            )
            Result.Success(declensions)
        } catch (e: Exception) {
            Result.Failure("Fail to filter by $declension ")
        }
    }

    suspend fun filterByFullSuffix(suffix: String) = withContext(Dispatchers.IO) {
        try {
            val declensions = declensionDao.getDeclension(suffix)
            Result.Success(declensions)
        } catch (e: Exception) {
            Result.Failure("Unable to filter declensions by suffix")
        }
    }

}