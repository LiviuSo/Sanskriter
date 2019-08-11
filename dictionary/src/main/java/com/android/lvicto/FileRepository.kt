package com.android.lvicto

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


object FileRepository {

    private val LOG_TAG = FileRepository::class.simpleName
    private const val FILENAME: String = "sanskrit_dic.json"

    fun saveToPrivateFile(context: Context, json: String): LiveData<() -> Unit> {
        Log.d(LOG_TAG, "Saving to file: $json")
        val ld: MutableLiveData<() -> Unit> = MutableLiveData()
        ld.value = {
            context.openFileOutput(FILENAME, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        }
        return ld
    }

    fun loadFromPrivateFile(context: Context): LiveData<String> {
        val ld = MutableLiveData<String>()
        val ba = arrayListOf<Byte>()
        try {
            context.openFileInput(FILENAME).use {
                Log.d(LOG_TAG, "Loading from file")
                do {
                    val c = it.read()
                    if (c == -1) {
                        break
                    }
                    ba.add(c.toByte())
                } while (true)
                ld.value = String(ba.toByteArray())
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "Exception loading from json")
            ld.value = ""
        }
        return ld
    }
}