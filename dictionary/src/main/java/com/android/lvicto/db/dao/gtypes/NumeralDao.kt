package com.android.lvicto.db.dao.gtypes

import androidx.room.*
import com.android.lvicto.common.Constants.TABLE_WORDS_NUMERALS
import com.android.lvicto.db.entity.gtypes.Numeral

@Dao
interface NumeralDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Numeral) // also updates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(words: List<Numeral>) // also updates

    @Query("DELETE FROM $TABLE_WORDS_NUMERALS")
    fun deleteAll()

    @Update
    fun update(word: Numeral)

    @Query("""
            UPDATE $TABLE_WORDS_NUMERALS 
            SET 
                gType=:gType,
                word=:word, 
                wordIAST=:wordIAST, 
                meaningEn=:meaningEn, 
                meaningRo=:meaningRo,
                gender=:gender,
                gCase=:gCase
            WHERE id = :id
    """)
    fun update(
        id: Long,
        gType: String,
        word: String, wordIAST: String, meaningEn: String, meaningRo: String,
        gender: String,
        gCase: String
    )

    @Query("SELECT * from $TABLE_WORDS_NUMERALS ORDER BY wordIAST ASC") // todo spike keep RX or LiveData
    fun getAllNumerals(): List<Numeral>

    @Query("SELECT * from $TABLE_WORDS_NUMERALS WHERE wordIAST LIKE :filterIAST ORDER BY wordIAST ASC")
    fun getNumerals(filterIAST: String): List<Numeral>

    @Query("""
       SELECT * from $TABLE_WORDS_NUMERALS 
        WHERE (meaningEn LIKE :filterEn) AND (wordIAST LIKE :filterIAST) 
        ORDER BY wordIAST ASC
    """)
    fun getNumerals(filterEn: String, filterIAST: String): List<Numeral>

    @Delete
    fun deleteNumerals(words: List<Numeral>): Int

}