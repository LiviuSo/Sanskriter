package com.android.lvicto.dependencyinjection.composition

import android.app.Application
import com.android.lvicto.common.ImportPickerCode
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.db.Converters
import com.android.lvicto.db.GrammarDatabase
import com.android.lvicto.db.WordsDatabase
import com.android.lvicto.db.dao.ConjugationDao
import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.dao.WordDao
import com.google.gson.Gson

class AppCompositionRoot(val application: Application) {

    // DBs
    private val grammarDB: GrammarDatabase = GrammarDatabase.getInstance(application)
    private val wordsDatabase: WordsDatabase = WordsDatabase.getInstance(application)

    // DAOs
    val conjugationDao: ConjugationDao by lazy {
        grammarDB.conjugationDao()
    }

    val declensionDao: DeclensionDao by lazy {
        grammarDB.declensionDao()
    }

    val wordDao: WordDao by lazy {
        wordsDatabase.wordDao()
    }

    // event bus
    val eventBus: ResultEventBus by lazy {
        ResultEventBus()
    }

    // gson
    val gson: Gson by lazy {
        Gson()
    }

    // import code holder todo: FIND A BETTER WAY
    val importCodeHolder by lazy {
        ImportPickerCode()
    }

    val converters: Converters by lazy {
        Converters()
    }

}
