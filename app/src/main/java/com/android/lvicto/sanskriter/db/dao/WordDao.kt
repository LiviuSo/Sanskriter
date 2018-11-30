package com.android.lvicto.sanskriter.db.dao

import android.arch.persistence.room.*
import com.android.lvicto.sanskriter.db.entity.Word
import io.reactivex.Single

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Word) // also updates

    @Query("DELETE FROM word_table")
    fun deleteAll()

//    @Query("SELECT * from word_table ORDER BY word ASC")
//    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * from word_table ORDER BY word ASC") // todo spike keep RX or LiveData
    fun getAllWords(): List<Word>

    @Delete
    fun deleteWords(words: List<Word>): Int
}