package com.android.lvicto.db.dao

import androidx.room.*
import com.android.lvicto.db.entity.Word

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Word) // also updates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(words: List<Word>) // also updates

    @Query("DELETE FROM word_table")
    fun deleteAll()

    @Update
    fun update(word: Word)

    @Query(
        """
            UPDATE word_table 
            SET word=:word, 
                wordIast=:wordIast, 
                meaningEn=:meaningEn, 
                meaningRo=:meaningRo,
                gType=:gType,
                paradigm=:paradigm,
                verbClass=:verbClass,
                gender=:gender
            WHERE id = :id
            """
    )
    fun update(
        id: Long,
        word: String,
        wordIast: String,
        meaningEn: String,
        meaningRo: String,
        gType: String,
        paradigm: String,
        verbClass: Int,
        gender: String
    )

    @Query("SELECT * from word_table ORDER BY wordIast ASC") // todo spike keep RX or LiveData
    fun getAllWords(): List<Word>

    @Query("SELECT * from word_table WHERE wordIAST LIKE :filterIast ORDER BY wordIAST ASC")
    fun getWords(filterIast: String): List<Word>

    @Query(
        """
       SELECT * from word_table 
        WHERE (meaningEn LIKE :filterEn) AND (wordIAST LIKE :filterIast) 
        ORDER BY wordIAST ASC
    """
    )
    fun getWords(filterEn: String, filterIast: String): List<Word>

    @Delete
    fun deleteWords(words: List<Word>): Int

    @Query(
        """
        SELECT * FROM word_table
        WHERE wordIAST LIKE :filter AND paradigm LIKE :paradigm
    """
    )
    fun getNounsAndAdjectives(filter: String, paradigm: String): List<Word>
}