package com.android.lvicto.sanskriter.viewmodels


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.android.lvicto.sanskriter.repositories.ChaptersRepository

class ChaptersViewModel : ViewModel() {

    private val repository: ChaptersRepository = ChaptersRepository()

    internal val chapterTitles: LiveData<ArrayList<String>>

    init {
        chapterTitles = repository.chapters
    }
}