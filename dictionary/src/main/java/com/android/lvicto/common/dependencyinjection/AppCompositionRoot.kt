package com.android.lvicto.common.dependencyinjection

import android.app.Application
import com.android.lvicto.db.GrammarDatabase
import com.android.lvicto.db.dao.ConjugationDao

class AppCompositionRoot(val application: Application) {
    // init the DB
    private val grammarDB: GrammarDatabase = GrammarDatabase.getInstance(application)

    // daos
    val conjugationDao: ConjugationDao by lazy {
        grammarDB.conjugationDao()
    }

}
