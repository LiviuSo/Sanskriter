package com.android.lvicto.sanskriter.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.repo.BookContentRepository

class ChaptersViewModel : ViewModel() {

    private val repository: BookContentRepository = BookContentRepository()

    val bookContents = MutableLiveData<BookContent>()

    init {
        repository.readBookContents(application).subscribe {
            bookContents.value = it
        }.dispose()
    }
}