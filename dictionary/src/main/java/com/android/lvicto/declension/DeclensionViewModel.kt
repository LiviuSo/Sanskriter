package com.android.lvicto.declension

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.lvicto.db.data.Declensions
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.common.FileRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeclensionViewModel(application: Application) : AndroidViewModel(application) {

    private val declensionRepo = DeclensionRepositoryImpl(application)
    private val fileRepo = FileRepository(application)

    fun getAll(): LiveData<List<Declension>> =
        MutableLiveData<List<Declension>>().also { liveData ->
            viewModelScope.launch(Dispatchers.IO) {
                liveData.postValue(declensionRepo.getAll())
            }
        }

    fun filter(declension: Declension): LiveData<List<Declension>> =
        MutableLiveData<List<Declension>>().also {
            viewModelScope.launch(Dispatchers.IO) {
                it.postValue(declensionRepo.filter(declension))
            }
        }

    fun detectDeclension(word: String): LiveData<List<Declension>> {

        return MutableLiveData<List<Declension>>().apply {
            viewModelScope.launch(Dispatchers.IO) {
                val declensions = arrayListOf<Declension>()

                if(word.isEmpty()) { // if empty return all declensions
                    declensionRepo.getAll().map {
                        declension -> declensions.add(declension)
                    }
                } else {
                    val size = word.length
                    var index = size - 1
                    while (index > 0) {
                        val suffixes =
                            declensionRepo.getSuffixes(word.substring(index, size)).distinct()
                        if (suffixes.isNotEmpty()) {
                            suffixes.map { suffix ->
                                declensionRepo.filterByFullSuffix(suffix).map { declension ->
                                    declensions.add(declension)
                                }
                            }
                        }
                        index--
                    }
                }
                this@apply.postValue(declensions)
            }
        }
    }

    fun delete(declension: Declension): LiveData<Boolean> = MutableLiveData<Boolean>().also {
        viewModelScope.launch(Dispatchers.IO) {
            it.postValue(declensionRepo.delete(declension) == 1)
        }
    }

    fun insert(declension: Declension): LiveData<Boolean> = MutableLiveData<Boolean>().also {
        viewModelScope.launch(Dispatchers.IO) {
            declensionRepo.insert(declension)
            it.postValue(true)
        }
    }

    fun readFromFile(uri: Uri): LiveData<List<Declension>> {
        return MutableLiveData<List<Declension>>().also {
            val launch = viewModelScope.launch(Dispatchers.IO) {
                val declensions = fileRepo.loadDeclensionsFromFile(uri)
                declensions.forEach {
                    declensionRepo.insert(it)
                }
                it.postValue(declensions)
            }
        }
    }

    // todo use when implement FileProvider
    fun writeToFile(declensions: Declensions, fileName: String): LiveData<Boolean> {
        return MutableLiveData<Boolean>().also {
            viewModelScope.launch {
                it.postValue(
                    fileRepo.writeDataToFile(
                        data = Gson().toJson(declensions),
                        fileName = fileName
                    )
                )
            }
        }
    }

}