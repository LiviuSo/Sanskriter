package com.android.lvicto.sanskriter.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask
import com.android.lvicto.sanskriter.db.dao.WordDao
import com.android.lvicto.sanskriter.db.entity.Word

@Database(entities = [Word::class], version = 1)
abstract class WordsDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        private var INSTANCE: WordsDatabase? = null

        fun getInstance(context: Context): WordsDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, WordsDatabase::class.java, "my_database.db").build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }


    // for testing
    fun popupateDbForTesting() {
        PopulateDbAsync(INSTANCE!!).execute()
    }

    private class PopulateDbAsync internal constructor(db: WordsDatabase) : AsyncTask<Void, Void, Void>() {

        private val mDao: WordDao = db.wordDao()

        override fun doInBackground(vararg params: Void): Void? {
            mDao.deleteAll()
            var word = Word( word = "priya")
            mDao.insert(word)
            word = Word( word = "ananda")
            mDao.insert(word)
            return null
        }
    }

}