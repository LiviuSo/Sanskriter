package com.lvicto.skeyboard.repo

import com.lvicto.skeyboard.data.Suggestion
import com.lvicto.skeyboard.suggestions.SuggestionProvider
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class SuggestionRepo {

    val allWords: Observable<List<Suggestion>> = Observable
            .fromCallable {  SuggestionProvider.getQwertySuggestions() }
            .subscribeOn(Schedulers.io())

}