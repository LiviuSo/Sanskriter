package com.android.lvicto.repo

import android.content.Context
import android.os.Environment
import android.util.Log
import com.android.lvicto.data.Words
import com.android.lvicto.db.entity.Word
import com.android.lvicto.util.getStorageDir
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.*


class FileRepository(val context: Context) {

    companion object {
        private val LOG_TAG = FileRepository::class.simpleName
    }

    suspend fun loadWordsFromFile(fileName: String): List<Word> = coroutineScope {
        withContext(Dispatchers.IO) {
            val json = readData(context = context, fileName = fileName)
            val words = Gson().fromJson(json, Words::class.java)
            words.list
        }
    }

    //write data to file
    suspend fun writeWordsToFile(context: Context, data: String, fileName: String): Boolean { // todo make async
        return coroutineScope {
            withContext(Dispatchers.IO) {
                var fileOutputStream: FileOutputStream? = null
                try {
                    val filePath = context.getStorageDir(fileName)
                    Log.d("readwrite", filePath)
                    fileOutputStream = FileOutputStream(filePath)
                    fileOutputStream.write(data.toByteArray())
                    true
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    false
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                } finally {
                    fileOutputStream?.close()
                }
            }
        }
    }

    //read data from the file
    private fun readData(context: Context, fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val filePath = context.getStorageDir(fileName)
            val fileInputStream = FileInputStream(filePath)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val reader = BufferedReader(inputStreamReader)
            var temp: String?
            while (reader.readLine().also { temp = it } != null) {
                stringBuilder.append(temp)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    //checks if external storage is available for read and write
    fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    //checks if external storage is available for read
    fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == state
    }
}