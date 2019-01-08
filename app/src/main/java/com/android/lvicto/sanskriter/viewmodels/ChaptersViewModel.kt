package com.android.lvicto.sanskriter.viewmodels


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.repositories.BookContentRepository
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.db.entity.Word

class ChaptersViewModel : ViewModel() {
    fun filter(s: String): LiveData<List<Word>> {
        val words = ArrayList<Word>()
        val data = MutableLiveData<List<Word>>()
        return data
    }

    private val repository: BookContentRepository = BookContentRepository()

    val bookContents = MutableLiveData<BookContent>()

    init {
        repository.readBookContents(application).subscribe {
            bookContents.value = it
        }.dispose()
    }
}