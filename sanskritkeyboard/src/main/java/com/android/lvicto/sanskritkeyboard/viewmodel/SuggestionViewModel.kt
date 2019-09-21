package com.android.lvicto.sanskritkeyboard.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.lvicto.sanskritkeyboard.data.Suggestion
import com.android.lvicto.sanskritkeyboard.repo.SuggestionRepo
import com.android.lvicto.sanskritkeyboard.utils.Constants.MAX_SUGGESTIONS
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class SuggestionViewModel {

    @SuppressLint("CheckResult")
    fun getSuggestions(string: String): Observable<List<Suggestion>> {
//        val suggestions: MutableLiveData<List<Suggestion>> = MutableLiveData() // todo restore using LiveData
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
//                .subscribe {
//                    suggestions.postValue(it)
//                }
//        return suggestions
    }
}