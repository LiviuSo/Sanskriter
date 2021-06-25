package com.android.lvicto.db.dao

import androidx.room.*
import com.android.lvicto.db.entity.Conjugation

@Dao
interface ConjugationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(conjugation: Conjugation)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(conjugations: List<Conjugation>)

    @Query("DELETE FROM conjugation_table")
    fun deleteAll()

    @Delete
    fun delete(conjugation: List<Conjugation>): Int

    @Query("SELECT * from conjugation_table ORDER BY paradigmRoot ASC")
    fun getAll(): List<Conjugation>

    @Query("""
        SELECT * from conjugation_table 
        WHERE (paradigmRoot LIKE :paradigmRoot) 
            AND (ending LIKE :ending) 
            AND (verbClass LIKE :verbClass) 
            AND (verbNumber LIKE :verbNumber) 
            AND (verbPerson LIKE :verbPerson) 
            AND (verbTime LIKE :verbTime) 
            AND (verbMode LIKE :verbMode) 
            AND (verbParadygmType LIKE :verbParadygmType) 
        ORDER BY paradigmRoot ASC""")
    fun getConjugations(
        paradigmRoot: String,
        ending: String,
        verbClass: String,
        verbNumber: String,
        verbPerson: String,
        verbTime: String,
        verbMode: String,
        verbParadygmType: String
    ): List<Conjugation>

}
