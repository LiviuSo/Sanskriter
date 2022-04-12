package com.android.lvicto.db.dao.gtypes

import androidx.room.*
import com.android.lvicto.common.Constants.TABLE_WORDS_PRONOUNS
import com.android.lvicto.db.entity.gtypes.Pronoun

@Dao
interface PronounDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Pronoun) // also updates

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(words: List<Pronoun>) // also updates

    @Query("DELETE FROM $TABLE_WORDS_PRONOUNS")
    fun deleteAll()

    @Update
    fun update(word: Pronoun)

    @Query("""
            UPDATE $TABLE_WORDS_PRONOUNS 
            SET 
                gType=:gType,
                word=:word,
                wordIAST=:wordIAST,
                meaningEn=:meaningEn,
                meaningRo=:meaningRo,
                paradigm=:paradigm,
                gender=:gender,
                number=:number,
                person=:person,
                gCase=:gCase
            WHERE id = :id
    """)
    fun update(
        id: Long,
        gType: String,
        word: String, wordIAST: String, meaningEn: String, meaningRo: String,
        paradigm: String,
        gender: String,
        number: String,
        person: String,
        gCase: String
    )

    @Query("SELECT * from $TABLE_WORDS_PRONOUNS ORDER BY wordIAST ASC")
    fun getAllPronouns(): List<Pronoun>

    @Query("SELECT * from $TABLE_WORDS_PRONOUNS WHERE wordIAST LIKE :filterIAST ORDER BY wordIAST ASC")
    fun getPronouns(filterIAST: String): List<Pronoun>

    @Query("""
       SELECT * from $TABLE_WORDS_PRONOUNS 
        WHERE (meaningEn LIKE :filterEn) AND (wordIAST LIKE :filterIAST) 
        ORDER BY wordIAST ASC
    """)
    fun getPronouns(filterEn: String, filterIAST: String): List<Pronoun>

    @Delete
    fun deletePronouns(words: List<Pronoun>): Int

    @Query("""
        SELECT * FROM $TABLE_WORDS_PRONOUNS
        WHERE wordIAST LIKE :filter AND paradigm LIKE :paradigm
    """)
    fun getPronounsByParadigm(filter: String, paradigm: String): List<Pronoun>
}