package com.android.lvicto.sanskriter.repo

import android.content.Context
import com.android.lvicto.common.readData
import com.android.lvicto.common.writeDataToFile

class NotesRepository(val context: Context) {

    // todo make suspend
    fun writeToFile(fileName: String, note: String) {
        context.writeDataToFile(note, fileName)
    }

    // todo make suspend
    fun readFromFile(fileName: String) : String {
        return context.readData(fileName)
    }

}