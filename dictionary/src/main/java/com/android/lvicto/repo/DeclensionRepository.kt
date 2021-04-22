package com.android.lvicto.repo

import com.android.lvicto.db.entity.Declension

interface DeclensionRepository {

    suspend fun getAll(): List<Declension>

    suspend fun insert(declension: Declension)

    suspend fun delete(declension: Declension): Int

    suspend fun filter(declension: Declension): List<Declension>

    suspend fun filterByFullSuffix(suffix: String): List<Declension>

    suspend fun getSuffixes(part: String): List<String>

}