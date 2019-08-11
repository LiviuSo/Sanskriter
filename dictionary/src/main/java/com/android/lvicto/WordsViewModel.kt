package com.android.lvicto

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.lvicto.db.entity.Word
import com.android.lvicto.dic.Words
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WordsViewModel(val app: Application) : AndroidViewModel(app) {

    private val repo: WordsRepository = WordsRepository(application = app)

    @SuppressLint("CheckResult")
    fun getAllWords() : LiveData<List<Word>> {
        val allWords: MutableLiveData<List<Word>> = MutableLiveData()
        repo.allWords.subscribe {
            allWords.postValue(it)
        }
        return allWords
    }

    @SuppressLint("CheckResult")
    fun insert(word: Word): LiveData<List<Word>> {
        val allWords: MutableLiveData<List<Word>> = MutableLiveData()
        repo.insertWordRx(word).subscribe {
            repo.allWords.subscribe {lw ->
                allWords.postValue(lw)
            }
        }
        return allWords
    }

    // todo use when implement FileProvider
    fun loadFromPrivateFile(): LiveData<String> =
            FileRepository.loadFromPrivateFile(app.applicationContext)

    // todo use when implement FileProvider
    fun saveToPrivateFile(words: Words): LiveData<() -> Unit> =
            FileRepository.saveToPrivateFile(context = app.applicationContext, json = Gson().toJson(words))

    @SuppressLint("CheckResult")
    fun loadFromString(json: String): LiveData<List<Word>> {
        val allWords: MutableLiveData<List<Word>> = MutableLiveData()
        val words = Gson().fromJson(json, Words::class.java)
        Observable.fromIterable(words.list)
                .flatMap { w ->
                    repo.insertWordRx(w).concatMap { Observable.just(w) }
                }
                .toList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    allWords.value = it
                }
        return allWords
    }

    @SuppressLint("CheckResult")
    fun deleteWords(words: List<Word>): LiveData<List<Word>> {
        val allWords: MutableLiveData<List<Word>> = MutableLiveData()
        repo.deleteWords(words = words)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it > 0) {
                        repo.allWords.subscribe { lw ->
                            allWords.postValue(lw)
                        }
                    }
                }
        return allWords
    }

    @SuppressLint("CheckResult")
    fun filterWords(substring: String): LiveData<List<Word>> { // todo improve
        val filteredWords = MutableLiveData<List<Word>>()
        repo.allWords
                .flatMap { words ->
                    Observable.fromIterable(words)
                }
                .filter {
                    val len = substring.length
                    val iast = it.wordIAST
                    if(iast.length >= len) {
                        it.wordIAST.substring(0, len) == substring
                    } else {
                        false
                    }
                }
                .toList()
                .toObservable().subscribe {
                    filteredWords.postValue(it)
                }
        return filteredWords
    }
}