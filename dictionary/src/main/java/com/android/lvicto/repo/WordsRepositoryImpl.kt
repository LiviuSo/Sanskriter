package com.android.lvicto.repo

import android.app.Application
import com.android.lvicto.db.WordsDatabase
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class WordsRepositoryImpl internal constructor(val application: Application) : WordsRepository {

    private val wordsDao: WordDao = WordsDatabase.getInstance(application).wordDao()

    override suspend fun getWords(): List<Word> = coroutineScope {
        withContext(Dispatchers.IO) {
            wordsDao.getAllWords()
        }
    }

    override suspend fun insertWord(word: Word): Int = coroutineScope {
        withContext(Dispatchers.IO) {
            wordsDao.insert(word)
            0
        }
    }

    override suspend fun update(id: Long, sans: String, iast: String, meaningEn: String, meaningRo: String) = coroutineScope {
        withContext(Dispatchers.IO) {
            wordsDao.update(Word(id, sans, iast, meaningEn, meaningRo))
            true // todo add
        }
    }

    override suspend fun deleteWords(words: List<Word>): Int = coroutineScope {
        withContext(Dispatchers.IO) {
            wordsDao.deleteWords(words)
        }
    }

    override suspend fun filter(key: String): List<Word> = coroutineScope {
        withContext(Dispatchers.IO) {
            wordsDao.getWords(key)
        }
    }

    override suspend fun filter(filterEn: String, filterIast: String): List<Word> = coroutineScope {
        withContext(Dispatchers.IO) {
            val filter1 = "%$filterEn%"
            val filter2 = "%$filterIast%"
            wordsDao.getWords(filter1, filter2)
        }
    }
}