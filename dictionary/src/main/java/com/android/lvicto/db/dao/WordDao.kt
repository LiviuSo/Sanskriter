package com.android.lvicto.db.dao

import androidx.room.*
import com.android.lvicto.common.Constants.WORDS_TABLE
import com.android.lvicto.db.entity.Word

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Word) // also updates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(words: List<Word>) // also updates

    @Query("DELETE FROM $WORDS_TABLE")
    fun deleteAll()

    @Update
    fun update(word: Word)

    @Query("""
            UPDATE $WORDS_TABLE 
            SET word=:word, 
                wordIAST=:wordIAST, 
                meaningEn=:meaningEn, 
                meaningRo=:meaningRo,
                gType=:gType,
                paradigm=:paradigm,
                verbClass=:verbClass,
                gender=:gender
            WHERE id = :id
    """)
    fun update(
        id: Long,
        word: String, wordIAST: String, meaningEn: String, meaningRo: String,
        gType: String,
        paradigm: String,
        verbClass: Int,
        gender: String
    )

    @Query("SELECT * from $WORDS_TABLE ORDER BY wordIAST ASC") // todo spike keep RX or LiveData
    fun getAllWords(): List<Word>

    @Query("SELECT * from $WORDS_TABLE WHERE wordIAST LIKE :filterIAST ORDER BY wordIAST ASC")
    fun getWords(filterIAST: String): List<Word>

    @Query("""
       SELECT * from $WORDS_TABLE 
        WHERE (meaningEn LIKE :filterEn) AND (wordIAST LIKE :filterIAST) 
        ORDER BY wordIAST ASC
    """)
    fun getWords(filterEn: String, filterIAST: String): List<Word>

    @Delete
    fun deleteWords(words: List<Word>): Int

    @Query("""
        SELECT * FROM $WORDS_TABLE
        WHERE wordIAST LIKE :filter AND paradigm LIKE :paradigm
    """)
    fun getNounsAndAdjectives(filter: String, paradigm: String): List<Word>
}