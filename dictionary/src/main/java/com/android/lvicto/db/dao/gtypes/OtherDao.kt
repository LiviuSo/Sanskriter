package com.android.lvicto.db.dao.gtypes

import androidx.room.*
import com.android.lvicto.common.Constants.TABLE_WORDS_OTHER
import com.android.lvicto.db.entity.gtypes.Other

@Dao
interface OtherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Other) // also updates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(words: List<Other>) // also updates

    @Query("DELETE FROM $TABLE_WORDS_OTHER")
    fun deleteAll()

    @Update
    fun update(word: Other)

    @Query("""
            UPDATE $TABLE_WORDS_OTHER 
            SET 
                gType=:gType,
                word=:word, 
                wordIAST=:wordIAST, 
                meaningEn=:meaningEn, 
                meaningRo=:meaningRo
            WHERE id = :id
    """)
    fun update(
        id: Long,
        gType: String,
        word: String, wordIAST: String, meaningEn: String, meaningRo: String
    )

    @Query("SELECT * from $TABLE_WORDS_OTHER ORDER BY wordIAST ASC")
    fun getAllOthers(): List<Other>

    @Query("SELECT * from $TABLE_WORDS_OTHER WHERE wordIAST LIKE :filterIAST ORDER BY wordIAST ASC")
    fun getOthers(filterIAST: String): List<Other>

    @Query("""
       SELECT * from $TABLE_WORDS_OTHER 
        WHERE (meaningEn LIKE :filterEn) AND (wordIAST LIKE :filterIAST) 
        ORDER BY wordIAST ASC
    """)
    fun getOthers(filterEn: String, filterIAST: String): List<Other>

    @Delete
    fun deleteOthers(words: List<Other>): Int

}