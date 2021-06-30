package com.android.lvicto.common

import android.content.Context
import android.net.Uri
import com.android.lvicto.db.data.Declensions
import com.android.lvicto.db.data.Words
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.db.entity.Word
import com.android.lvicto.common.util.readData
import com.android.lvicto.common.util.writeDataToFile
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

    suspend fun loadDeclensionsFromFile(uri: Uri): List<Declension> = coroutineScope {
        withContext(Dispatchers.IO) {
            val json = context.readData(uri = uri)
            val declensions = Gson().fromJson(json, Declensions::class.java)
            declensions.declensions
        }
    }

    suspend fun writeDataToFile(data: String, fileName: String): Boolean { // todo make async
        return coroutineScope {
            withContext(Dispatchers.IO) {
                context.writeDataToFile(data, fileName)
            }
        }
    }
}