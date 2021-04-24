package com.android.lvicto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.entity.Word

@Database(entities = [Word::class], version = 3)
@TypeConverters(Converters::class)
abstract class WordsDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        private var INSTANCE: WordsDatabase? = null

        fun getInstance(context: Context): WordsDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WordsDatabase::class.java,
                    "my_database.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
            }
            return INSTANCE as WordsDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE word_table ADD COLUMN gType TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE word_table ADD COLUMN paradigm TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE word_table ADD COLUMN verbClass INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `words_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `word` TEXT NOT NULL, `wordIAST` TEXT NOT NULL, `meaningEn` TEXT NOT NULL, `meaningRo` TEXT NOT NULL, `gType` TEXT NOT NULL, `paradigm` TEXT NOT NULL, `verbClass` INTEGER NOT NULL)
                """)

                // Copy the data
                database.execSQL("""
                    INSERT INTO words_new (id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, verbClass)
                    SELECT id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, verbClass
                        FROM word_table
                    """)

                // Remove the old table
                database.execSQL("DROP TABLE word_table")

                // Change the table name to the correct one
                database.execSQL("ALTER TABLE words_new RENAME TO word_table")
            }
        }
    }
}