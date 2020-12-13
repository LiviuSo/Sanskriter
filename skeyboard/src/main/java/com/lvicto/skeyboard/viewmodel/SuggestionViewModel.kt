package com.lvicto.skeyboard.viewmodel

import android.annotation.SuppressLint
import com.lvicto.skeyboard.data.Suggestion
import com.lvicto.skeyboard.repo.SuggestionRepo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class SuggestionViewModel {

    @SuppressLint("CheckResult")
    fun getSuggestions(string: String): Observable<List<Suggestion>> { // todo make it a coroutine
        return SuggestionRepo().allWords
                .flatMap {
                    Observable.fromIterable(it)
                }
                .sorted {x, y -> x.rank - y.rank}
                .filter {
                    it.word.startsWith(string)
                }
                .take(MAX_SUGGESTIONS)
                .toList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        const val MAX_SUGGESTIONS = 10L // todo make it a setting
    }
}