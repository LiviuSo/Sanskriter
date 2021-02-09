package com.android.lvicto.sanskriter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.lvicto.sanskriter.repo.NotesRepository

class NotesViewModel(val context: Application) : AndroidViewModel(context) {

    private val fileName = "notes.json"
    private val repoNotes = NotesRepository(context)

    fun saveNote(note: String) {
        repoNotes.writeToFile(fileName, note)
    }

    fun readNote() : String {
        return repoNotes.readFromFile(fileName)
    }

}
