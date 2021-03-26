package com.android.lvicto.repo

import com.android.lvicto.db.entity.Word

interface WordsRepository {

    suspend fun getWords(): List<Word>

    suspend fun insertWord(word: Word): Int

    suspend fun update(id: Long, sans: String, iast: String, meaningEn: String, meaningRo: String) : Boolean
    suspend fun deleteWords(words: List<Word>): Int

    suspend fun filter(key: String, isPrefix: Boolean = false): List<Word>

    suspend fun filter2(filterEn: String, filterIast: String): List<Word>

}