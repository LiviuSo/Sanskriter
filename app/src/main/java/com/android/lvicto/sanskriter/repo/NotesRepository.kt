package com.android.lvicto.sanskriter.repo

import android.content.Context
import android.widget.Toast
import com.android.lvicto.util.readData
import com.android.lvicto.util.writeWordsToFile

class NotesRepository(val context: Context) {

    // todo make suspend
    fun writeToFile(fileName: String, note: String) {
        context.writeWordsToFile(note, fileName)
    }

    // todo make suspend
    fun readFromFile(fileName: String) : String {
        return context.readData(fileName)
    }

}
