package com.android.lvicto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.lvicto.db.dao.gtypes.*
import com.android.lvicto.db.entity.gtypes.*

@Database(
    entities = [Numeral::class, Other::class, Pronoun::class, Substantive::class, Verb::class],
    version = 5)
@TypeConverters(Converters::class)
abstract class WordsDatabase : RoomDatabase() {

    abstract fun numeralDao(): NumeralDao
    abstract fun otherDao(): OtherDao
    abstract fun pronounDao(): PronounDao
    abstract fun substantiveDao(): SubstantiveDao
    abstract fun verbDao(): VerbDao

    companion object {
        private var INSTANCE: WordsDatabase? = null

        fun getInstance(context: Context): WordsDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WordsDatabase::class.java,
                    "words_database.db"
                )
//                    .addMigrations(
//                     MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4
//                    MIGRATION_4_5
//                ) // todo investigate to execute asynch
             .build()
            }
            return INSTANCE as WordsDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }
}