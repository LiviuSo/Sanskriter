package com.android.lvicto

import android.app.Application
import com.android.lvicto.db.WordsDatabase
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.entity.Word
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class WordsRepository  internal constructor(val application: Application) {

    private val wordsDao: WordDao = WordsDatabase.getInstance(application)!!.wordDao()
    val allWords: Observable<List<Word>>

    init {
        allWords = Observable.fromCallable {  wordsDao.getAllWords() }.subscribeOn(Schedulers.io())
    }

    fun insertWordRx(word: Word): Observable<Int> {
        return Observable.fromCallable {
            wordsDao.insert(word)
            0
        }.subscribeOn(Schedulers.io())
    }

    fun deleteWords(words: List<Word>): Observable<Int> {
        return Observable.fromCallable { wordsDao.deleteWords(words) }
    }
}