package com.android.lvicto.db.dao.gtypes

import androidx.room.*
import com.android.lvicto.common.Constants.TABLE_WORDS_SUBSTANTIVES
import com.android.lvicto.db.entity.gtypes.Substantive

@Dao
interface SubstantiveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Substantive) // also updates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(words: List<Substantive>) // also updates

    @Query("DELETE FROM $TABLE_WORDS_SUBSTANTIVES")
    fun deleteAll()

    @Update
    fun update(word: Substantive)

    @Query("""
            UPDATE $TABLE_WORDS_SUBSTANTIVES 
            SET 
                gType=:gType,
                word=:word, 
                wordIAST=:wordIAST, 
                meaningEn=:meaningEn, 
                meaningRo=:meaningRo,
                paradigm=:paradigm,
                gender=:gender
            WHERE id = :id
    """)
    fun update(
        id: Long,
        gType: String,
        word: String,
        wordIAST: String,
        meaningEn: String,
        meaningRo: String,
        paradigm: String,
        gender: String
    )

    @Query("SELECT * from $TABLE_WORDS_SUBSTANTIVES ORDER BY wordIAST ASC") // todo spike keep RX or LiveData
    fun getAllSubstantives(): List<Substantive>

    @Query("SELECT * from $TABLE_WORDS_SUBSTANTIVES WHERE wordIAST LIKE :filterIAST ORDER BY wordIAST ASC")
    fun getSubstantives(filterIAST: String): List<Substantive>

    @Query("""
       SELECT * from $TABLE_WORDS_SUBSTANTIVES 
       WHERE (meaningEn LIKE :filterEn) AND (wordIAST LIKE :filterIAST) 
       ORDER BY wordIAST ASC
    """)
    fun getSubstantives(filterEn: String, filterIAST: String): List<Substantive>

    @Delete
    fun deleteSubstantives(words: List<Substantive>): Int

    @Query("""
        SELECT * FROM $TABLE_WORDS_SUBSTANTIVES
        WHERE wordIAST LIKE :filter AND paradigm LIKE :paradigm
    """)
    fun getSubstantivesByParadigm(filter: String, paradigm: String): List<Substantive>
}