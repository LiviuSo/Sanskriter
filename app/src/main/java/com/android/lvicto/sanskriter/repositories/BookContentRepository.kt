package com.android.lvicto.sanskriter.repositories

import android.content.Context
import com.google.gson.Gson
import io.reactivex.Observable
import com.android.lvicto.sanskriter.data.BookContent
import java.io.InputStreamReader
import java.lang.StringBuilder

class BookContentRepository {

    fun readBookContents(context: Context): Observable<BookContent> {
        val gson = Gson()
        val inputStreamReader = InputStreamReader(context.assets.open("json/coulson_contents.json"))
        val stringBuilder = StringBuilder()
        inputStreamReader.forEachLine {
            stringBuilder.append(it)
        }
        return Observable.just(gson.fromJson(stringBuilder.toString(), BookContent::class.java))
    }

}
