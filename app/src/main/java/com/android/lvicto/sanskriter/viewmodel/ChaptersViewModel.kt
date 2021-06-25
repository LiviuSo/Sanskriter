package com.android.lvicto.sanskriter.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.lvicto.db.entity.Word
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.repo.BookContentRepository
import com.android.lvicto.sanskriter.data.BookContent

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