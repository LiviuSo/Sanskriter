package com.android.lvicto.words

import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.db.data.VerbClass
import com.android.lvicto.db.entity.Word

interface WordsRepository {

    suspend fun getWords(): List<Word>

    suspend fun insertWord(word: Word): Int

    suspend fun update(
        id: Long,
        sans: String,
        iast: String,
        meaningEn: String,
        meaningRo: String,
        gType: GrammaticalType,
        paradigm: String,
        verbClass: VerbClass,
        gender: GrammaticalGender
    ) : Boolean
    suspend fun deleteWords(words: List<Word>): Int

    suspend fun filter(key: String, isPrefix: Boolean = false): List<Word>

    suspend fun filter2(filterEn: String, filterIast: String): List<Word>

    suspend fun filterNounsAndAdjectives(root: String, paradigm: String, prefix: Boolean): List<Word>

}