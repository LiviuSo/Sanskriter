package com.android.lvicto.repo

import android.content.Context
import android.net.Uri
import com.android.lvicto.data.Words
import com.android.lvicto.db.entity.Word
import com.android.lvicto.util.readData
import com.android.lvicto.util.writeWordsToFile
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext


class FileRepository(val context: Context) {

    companion object {
        private val LOG_TAG = FileRepository::class.simpleName
    }

    suspend fun loadWordsFromFile(uri: Uri): List<Word> = coroutineScope {
        withContext(Dispatchers.IO) {
            val json = context.readData(uri = uri)
            val words = Gson().fromJson(json, Words::class.java)
            words.list
        }
    }

    suspend fun writeWordsToFile(data: String, fileName: String): Boolean { // todo make async
        return coroutineScope {
            withContext(Dispatchers.IO) {
                context.writeWordsToFile(data, fileName)
            }
        }
    }
}