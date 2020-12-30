package com.android.lvicto.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.lvicto.db.entity.Word
import com.android.lvicto.data.Words
import com.android.lvicto.repo.FileRepository
import com.android.lvicto.repo.WordsRepository
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WordsViewModel(val app: Application) : AndroidViewModel(app) {

    private val repo: WordsRepository = WordsRepository(application = app)

    @SuppressLint("CheckResult")
    fun getAllWords(): LiveData<List<Word>> {
        val allWords: MutableLiveData<List<Word>> = MutableLiveData()
        repo.allWords.subscribe {
            allWords.postValue(it)
        }
        return allWords
    }

    @SuppressLint("CheckResult")
    fun insert(word: Word): LiveData<Boolean> {
        val success: MutableLiveData<Boolean> = MutableLiveData()
        repo.insertWordRx(word).subscribe {
                success.postValue(true)
            }
        return success
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
    fun update(id: Long, sans: String, iast: String, meaningEn: String, meaningRo: String): LiveData<Boolean> {
        val result: MutableLiveData<Boolean> = MutableLiveData()
        repo.update(id, sans, iast, meaningEn, meaningRo).subscribe {
            result.postValue(true)
        }
        return result
    }

    @SuppressLint("CheckResult")
    fun filter(filter: String): LiveData<List<Word>> {
        val filteredWords = MutableLiveData<List<Word>>()
        if(filter.isNotBlank() && filter.isNotEmpty()) {
            repo.filter(filter)
        } else {
            repo.allWords
        }.subscribe {
            filteredWords.postValue(it)
        }
        return filteredWords
    }

    @SuppressLint("CheckResult")
    fun filter(filterEn: String, filterIast: String): LiveData<List<Word>> {
        val filteredWords = MutableLiveData<List<Word>>()
        repo.filter(filterEn, filterIast).subscribe {
            filteredWords.postValue(it)
        }
        return filteredWords
    }

}