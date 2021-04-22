package com.android.lvicto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.lvicto.db.dao.DeclensionDao
import com.android.lvicto.db.entity.Declension

@Database(entities = [Declension::class], version = 1)
@TypeConverters(Converters::class)
abstract class DeclensionDatabase  : RoomDatabase() {

    abstract fun declensionDao(): DeclensionDao

    companion object {
        private var INSTANCE: DeclensionDatabase? = null

        fun getInstance(context: Context): DeclensionDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, DeclensionDatabase::class.java, "declension_database.db").build()
            }
            return INSTANCE as DeclensionDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}