package com.android.lvicto.sanskriter.repositories

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.android.lvicto.sanskriter.db.WordsDatabase
import com.android.lvicto.sanskriter.db.dao.WordDao
import com.android.lvicto.sanskriter.db.entity.Word
import io.reactivex.Single

class WordsRepository  internal constructor(val application: Application) {

    private val wordsDao: WordDao = WordsDatabase.getInstance(application)!!.wordDao()
    val allWords: LiveData<List<Word>>
//    val allWords: Single<List<Word>>

    init {
        allWords = wordsDao.getAllWords()
//        allWords = Single.fromCallable {
//            wordsDao.getAllWords()
//        }
    }

    fun insertWord(word: Word) {
        InsertAsyncTask(mAsyncTaskDao = wordsDao).execute(word)
    }

    fun deleteWords(words: List<Word>): Single<Int> {
        return Single.fromCallable { wordsDao.deleteWords(words) }
    }

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: WordDao)
        : AsyncTask<Word, Void, Void>() {

        override fun doInBackground(vararg params: Word): Void? {
            mAsyncTaskDao.insert(params[0])
            return null
        }
    }
}