package com.android.lvicto.db.dao.gtypes

import androidx.room.*
import com.android.lvicto.common.Constants.TABLE_WORDS_VERBS
import com.android.lvicto.db.entity.gtypes.Verb

@Dao
interface VerbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Verb) // also updates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(words: List<Verb>) // also updates

    @Query("DELETE FROM $TABLE_WORDS_VERBS")
    fun deleteAll()

    @Update
    fun update(word: Verb)

    @Query("""
            UPDATE $TABLE_WORDS_VERBS 
            SET 
                gType=:gType,
                word=:word, 
                wordIAST=:wordIAST, 
                meaningEn=:meaningEn, 
                meaningRo=:meaningRo,
                paradigm=:paradigm,
                verbClass=:verbClass
            WHERE id = :id
    """)
    fun update(
        id: Long,
        gType: String,
        word: String, wordIAST: String, meaningEn: String, meaningRo: String,
        paradigm: String,
        verbClass: Int,
    )

    @Query("SELECT * from $TABLE_WORDS_VERBS ORDER BY wordIAST ASC") // todo spike keep RX or LiveData
    fun getAllVerbs(): List<Verb>

    @Query("SELECT * from $TABLE_WORDS_VERBS WHERE wordIAST LIKE :filterIAST ORDER BY wordIAST ASC")
    fun getVerbs(filterIAST: String): List<Verb>

    @Query("""
       SELECT * from $TABLE_WORDS_VERBS 
        WHERE (meaningEn LIKE :filterEn) AND (wordIAST LIKE :filterIAST) 
        ORDER BY wordIAST ASC
    """)
    fun getVerbs(filterEn: String, filterIAST: String): List<Verb>

    @Delete
    fun deleteVerbs(words: List<Verb>): Int

    @Query("""
        SELECT * FROM $TABLE_WORDS_VERBS
        WHERE wordIAST LIKE :filter AND paradigm LIKE :paradigm
    """)
    fun getNounsAndAdjectives(filter: String, paradigm: String): List<Verb>
}