package com.android.lvicto.sanskriter.viewmodels


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.repositories.BookContentRepository
import com.android.lvicto.sanskriter.data.BookContent

class ChaptersViewModel : ViewModel() {

    private val repository: BookContentRepository = BookContentRepository()

    val bookContents = MutableLiveData<BookContent>()

    init {
        repository.readBookContents(application).subscribe {
            bookContents.value = it
        }.dispose()
    }
}