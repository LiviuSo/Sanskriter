package com.android.lvicto.sanskriter.db

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
            var word = Word( word = "priya", wordIAST = "")
            mDao.insert(word)
            word = Word( word = "ananda", wordIAST = "")
            mDao.insert(word)
            return null
        }
    }

}