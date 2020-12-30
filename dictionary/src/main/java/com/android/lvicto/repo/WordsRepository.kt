package com.android.lvicto.repo

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

    fun update(id: Long, sans: String, iast: String, meaningEn: String, meaningRo: String): Observable<Unit> {
        return Observable.fromCallable {
            wordsDao.update(Word(id, sans, iast, meaningEn, meaningRo))
        }.subscribeOn(Schedulers.io())
    }

    fun deleteWords(words: List<Word>): Observable<Int> {
        return Observable.fromCallable {
            wordsDao.deleteWords(words)
        }.subscribeOn(Schedulers.io())
    }

    fun filter(key: String): Observable<List<Word>> {
        return Observable.fromCallable {
            val filter = "%$key%"
            wordsDao.getWords(filter)
        }.subscribeOn(Schedulers.io())
    }

    fun filter(filterEn: String, filterIast: String): Observable<List<Word>> {
        return Observable.fromCallable {
            val filter1 = "%$filterEn%"
            val filter2 = "%$filterIast%"
            wordsDao.getWords(filter1, filter2)
        }.subscribeOn(Schedulers.io())
    }
}