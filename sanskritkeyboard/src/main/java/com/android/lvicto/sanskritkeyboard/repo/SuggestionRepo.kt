package com.android.lvicto.sanskritkeyboard.repo

import com.android.lvicto.sanskritkeyboard.data.Suggestion
import com.android.lvicto.sanskritkeyboard.data.SuggestionProvider
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class SuggestionRepo {

    val allWords: Observable<List<Suggestion>> = Observable
            .fromCallable {  SuggestionProvider.getQwertySuggestions() }
            .subscribeOn(Schedulers.io())

}