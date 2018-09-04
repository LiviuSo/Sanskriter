package com.android.lvicto.sanskriter.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.lvicto.sanskriter.data.Words
import com.android.lvicto.sanskriter.db.entity.Word
import com.android.lvicto.sanskriter.repositories.FileRepository
import com.android.lvicto.sanskriter.repositories.WordsRepository
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class WordsViewModel(val app: Application) : AndroidViewModel(app) {

    private val repo: WordsRepository = WordsRepository(application = app)
    val allWords: LiveData<List<Word>>
//    val allWords: Single<List<Word>>

    init {
        allWords = repo.allWords
//        allWords = repo.allWords.subscribeOn(Schedulers.io())
    }

    fun insert(word: Word) {
        repo.insertWord(word)
    }

    // todo use when implement FileProvider
    fun loadFromPrivateFile() : LiveData<String> =
            FileRepository.loadFromPrivateFile(app.applicationContext)

    // todo use when implement FileProvider
    fun saveToPrivateFile(words: Words) : LiveData<()->Unit> =
            FileRepository.saveToPrivateFile(context = app.applicationContext, json = Gson().toJson(words))

    fun loadFromString(json: String): LiveData<String> {
        val mutableLiveData: MutableLiveData<String> = MutableLiveData()
        mutableLiveData.value = json
        return mutableLiveData
    }

    fun deleteWords(words: List<Word>): Single<Int> {
        return repo.deleteWords(words = words).subscribeOn(Schedulers.io())
    }
}