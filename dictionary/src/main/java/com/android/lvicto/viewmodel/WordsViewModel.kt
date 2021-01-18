package com.android.lvicto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.lvicto.data.Words
import com.android.lvicto.db.entity.Word
import com.android.lvicto.repo.FileRepository
import com.android.lvicto.repo.WordsRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.*

class WordsViewModel(val app: Application) : AndroidViewModel(app) {

    private val repoWords = WordsRepositoryImpl(app)
    private val repoFile = FileRepository(app)

    fun getAllWordsCor(): LiveData<List<Word>> {
        val allWords: MutableLiveData<List<Word>> = MutableLiveData()
        GlobalScope.launch(Dispatchers.IO) {
            allWords.postValue(repoWords.getWords())
        }
        return allWords
    }

    fun insert(word: Word): LiveData<Boolean> {
        val success: MutableLiveData<Boolean> = MutableLiveData()
        GlobalScope.launch(Dispatchers.IO) {
            success.postValue(repoWords.insertWord(word) == 0)
        }
        return success
    }

    fun readFromFiles(fileName: String): LiveData<List<Word>> {
        return MutableLiveData<List<Word>>().also {
            GlobalScope.launch {
                val words = repoFile.loadWordsFromFile(fileName)
                words.forEach {
                    repoWords.insertWord(it)
                }
                it.postValue(words)
            }
        }
    }

    // todo use when implement FileProvider
    fun writeToFiles(words: Words, fileName: String): LiveData<Boolean> {
        return MutableLiveData<Boolean>().also {
            GlobalScope.launch {
                it.postValue(
                        repoFile.writeWordsToFile(context = app.applicationContext,
                                data = Gson().toJson(words),
                                fileName = fileName))
            }
        }
    }

    fun deleteWords(words: List<Word>): LiveData<List<Word>> = MutableLiveData<List<Word>>().also {
        GlobalScope.launch {
            repoWords.deleteWords(words = words)
            it.postValue(repoWords.getWords())
        }
    }

    fun update(id: Long, sans: String, iast: String, meaningEn: String, meaningRo: String): LiveData<Boolean> =
            MutableLiveData<Boolean>().also {
                GlobalScope.launch {
                    val successful: Boolean = repoWords.update(id, sans, iast, meaningEn, meaningRo)
                    it.postValue(successful)
                }
            }

    fun filter(filter: String): LiveData<List<Word>> = MutableLiveData<List<Word>>().also {
        GlobalScope.launch {
            it.postValue(if (filter.isNotBlank() && filter.isNotEmpty()) {
                repoWords.filter(filter)
            } else {
                repoWords.getWords()
            })
        }
    }

    fun filter(filterEn: String, filterIast: String): LiveData<List<Word>> =
            MutableLiveData<List<Word>>().also {
                GlobalScope.launch {
                    it.postValue(repoWords.filter(filterEn, filterIast))
                }
            }
}