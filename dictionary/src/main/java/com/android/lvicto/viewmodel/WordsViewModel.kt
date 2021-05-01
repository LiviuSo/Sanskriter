package com.android.lvicto.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.lvicto.data.GrammaticalType
import com.android.lvicto.data.VerbClass
import com.android.lvicto.data.Words
import com.android.lvicto.db.entity.Word
import com.android.lvicto.repo.FileRepository
import com.android.lvicto.repo.WordsRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordsViewModel(val app: Application) : AndroidViewModel(app) {

    private val repoWords = WordsRepositoryImpl(app)
    private val repoFile = FileRepository(app)

    fun getAllWordsCor(): LiveData<List<Word>> {
        val allWords: MutableLiveData<List<Word>> = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO) {
            allWords.postValue(repoWords.getWords())
        }
        return allWords
    }

    fun insert(word: Word): LiveData<Boolean> {
        val success: MutableLiveData<Boolean> = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO) {
            success.postValue(repoWords.insertWord(word) == 0)
        }
        return success
    }

    fun readFromFiles(uri: Uri): LiveData<List<Word>> {
        return MutableLiveData<List<Word>>().also {
            viewModelScope.launch {
                val words = repoFile.loadWordsFromFile(uri)
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
            viewModelScope.launch {
                it.postValue(repoFile.writeDataToFile(data = Gson().toJson(words), fileName = fileName))
            }
        }
    }

    fun deleteWords(words: List<Word>): LiveData<List<Word>> = MutableLiveData<List<Word>>().also {
        viewModelScope.launch {
            repoWords.deleteWords(words = words)
            it.postValue(repoWords.getWords())
        }
    }

    fun update(
        id: Long,
        sans: String,
        iast: String,
        meaningEn: String,
        meaningRo: String,
        gType: GrammaticalType,
        paradigm: String,
        verbClass: VerbClass
    ): LiveData<Boolean> =
            MutableLiveData<Boolean>().also {
                viewModelScope.launch {
                    val successful: Boolean = repoWords.update(id, sans, iast, meaningEn, meaningRo, gType, paradigm, verbClass)
                    it.postValue(successful)
                }
            }

    fun filter(filter: String, isPrefix: Boolean): LiveData<List<Word>> = MutableLiveData<List<Word>>().also {
        viewModelScope.launch {
            it.postValue(if (filter.isNotBlank() && filter.isNotEmpty()) {
                repoWords.filter(filter, isPrefix)
            } else {
                repoWords.getWords()
            })
        }
    }

    fun filter2(filterEn: String, filterIast: String): LiveData<List<Word>> =
            MutableLiveData<List<Word>>().also {
                viewModelScope.launch {
                    it.postValue(repoWords.filter2(filterEn, filterIast))
                }
            }

    fun filter(root: String, paradigm: String, isPrefix: Boolean): LiveData<List<Word>> {
        return MutableLiveData<List<Word>>().also {
            viewModelScope.launch {
                it.postValue(repoWords.filterNounsAndAdjectives(root, paradigm, isPrefix))
            }
        }
    }
}