package com.android.lvicto.sanskriter.repositories

import android.app.Application
import android.os.AsyncTask
import com.android.lvicto.sanskriter.db.WordsDatabase
import com.android.lvicto.sanskriter.db.dao.WordDao
import com.android.lvicto.sanskriter.db.entity.Word
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class WordsRepository  internal constructor(val application: Application) {

    private val wordsDao: WordDao = WordsDatabase.getInstance(application)!!.wordDao()
    val allWords: Observable<List<Word>>

    init {
        allWords = Observable.fromCallable {  wordsDao.getAllWords() }.subscribeOn(Schedulers.io())
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