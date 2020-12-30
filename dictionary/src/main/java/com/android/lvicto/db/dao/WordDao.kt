package com.android.lvicto.db.dao

import androidx.room.*
import com.android.lvicto.db.entity.Word

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(word: Word) // also updates

    @Query("DELETE FROM word_table")
    fun deleteAll()

    @Update
    fun update(word: Word)

    @Query("SELECT * from word_table ORDER BY wordIast ASC") // todo spike keep RX or LiveData
    fun getAllWords(): List<Word>

    @Query("SELECT * from word_table WHERE wordIAST LIKE :filter ORDER BY wordIAST ASC")
    fun getWords(filter: String): List<Word>

    @Query("SELECT * from word_table WHERE (meaningEn LIKE :filterEn) AND (wordIAST LIKE :filterIast) ORDER BY wordIAST ASC")
    fun getWords(filterEn: String, filterIast: String): List<Word>

    @Delete
    fun deleteWords(words: List<Word>): Int
}