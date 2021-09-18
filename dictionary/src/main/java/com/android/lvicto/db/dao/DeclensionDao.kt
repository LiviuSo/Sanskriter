package com.android.lvicto.db.dao

import androidx.room.*
import com.android.lvicto.db.entity.Declension

@Dao
interface DeclensionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(suffix: Declension)

    @Insert
    fun insert(suffix: List<Declension>)

    @Query("DELETE FROM declension_table")
    fun deleteAll()

    @Delete
    fun deleteDeclension(declension: List<Declension>): Int

    @Query("SELECT * from declension_table ORDER BY paradigm ASC")
    fun getAll(): List<Declension>

    @Query("""
        SELECT * from declension_table 
        WHERE (paradigm LIKE :paradigm) 
            AND (paradigmEnding LIKE :paradigmEnding) 
            AND (suffix LIKE :suffix) 
            AND (gCase LIKE :gCase) 
            AND (gNumber LIKE :gNumber) 
            AND (gGender LIKE :gGender) 
        ORDER BY paradigm ASC""")
    fun getDeclensions(
        paradigm: String,
        paradigmEnding: String,
        suffix: String,
        gCase: String,
        gNumber: String,
        gGender: String
    ): List<Declension>

    @Query("SELECT * from declension_table WHERE (suffix LIKE :suffix) ORDER BY paradigm ASC")
    fun getDeclension(suffix: String): List<Declension>

    @Query("SELECT suffix from declension_table WHERE suffix LIKE :part")
    fun getSuffixes(part:String): List<String>

}