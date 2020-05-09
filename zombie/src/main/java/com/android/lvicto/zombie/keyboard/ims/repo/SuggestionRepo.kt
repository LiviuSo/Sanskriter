package com.android.lvicto.zombie.keyboard.ims.repo

import com.android.lvicto.zombie.keyboard.ims.data.Suggestion
import com.android.lvicto.zombie.keyboard.ims.suggestions.SuggestionProvider
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class SuggestionRepo {

    val allWords: Observable<List<Suggestion>> = Observable
            .fromCallable {  SuggestionProvider.getQwertySuggestions() }
            .subscribeOn(Schedulers.io())

}