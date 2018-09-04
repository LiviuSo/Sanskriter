package com.android.lvicto.sanskriter.repositories

import android.arch.lifecycle.MutableLiveData
import com.android.lvicto.sanskriter.source.TitlesProvider

class ChaptersRepository {

    val chapters: MutableLiveData<ArrayList<String>> = MutableLiveData()

    init {
        chapters.value = TitlesProvider.generateChapterTitles()
    }
}
