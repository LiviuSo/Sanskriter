package com.android.lvicto.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.lvicto.db.dao.ConjugationDao
import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.entity.Conjugation
import com.android.lvicto.db.entity.Declension

@Database(entities = [Declension::class, Conjugation::class], version = 2)
@TypeConverters(Converters::class)
abstract class GrammarDatabase  : RoomDatabase() {

    abstract fun declensionDao(): DeclensionDao
    abstract fun conjugationDao(): ConjugationDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `conjugation_table` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `paradigmRoot` TEXT, 
                        `ending` TEXT, 
                        `verbClass` TEXT, 
                        `verbNumber` TEXT, 
                        `verbPerson` TEXT, 
                        `verbMode` TEXT, 
                        `verbParadygmType` TEXT)
                """)
            }
        }

        private var INSTANCE: GrammarDatabase? = null

        fun getInstance(context: Context): GrammarDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    GrammarDatabase::class.java,
                    "grammar_database.db")
//                    .addMigrations(MIGRATION_1_2) // todo investigate to execute asynch
                    .build()
            }
            return INSTANCE as GrammarDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}