package com.android.lvicto.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.lvicto.data.Words
import com.android.lvicto.db.entity.Word
import com.android.lvicto.repo.FileRepository
import com.android.lvicto.repo.WordsRepository
import com.android.lvicto.util.Constants
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File

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

    fun readFromfiles(fileName: String): LiveData<List<Word>> {
        return loadFromString(FileRepository.readData(context = app.applicationContext, fileName = fileName))
    }

    // todo use when implement FileProvider
    fun writeToFiles(words: Words, fileName: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        result.postValue(
                FileRepository.writeData(context = app.applicationContext,
                        data = Gson().toJson(words),
                        fileName = fileName))
        return result
    }

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

    fun export(context: Context, filename: String) {
        val file = File(FileRepository.getStorageDir(context, filename))
//        val path: Uri = Uri.fromFile(file)
        val path = FileProvider.getUriForFile(context,
                context.applicationContext.packageName + ".provider",
                file)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // set the type to 'email'
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf(Constants.Dictionary.EMAIL_RECIPIENT)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, Constants.Dictionary.EMAIL_SUBJECT)
        context.startActivity(Intent.createChooser(emailIntent, Constants.Dictionary.EMAIL_TITLE))
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
        if (filter.isNotBlank() && filter.isNotEmpty()) {
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