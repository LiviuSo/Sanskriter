package com.android.lvicto.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object WordsMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE word_table ADD COLUMN gType TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE word_table ADD COLUMN paradigm TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE word_table ADD COLUMN verbClass INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS words_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        word TEXT NOT NULL, wordIAST TEXT NOT NULL, 
                        meaningEn TEXT NOT NULL, meaningRo TEXT NOT NULL, 
                        gType TEXT NOT NULL, paradigm TEXT NOT NULL, 
                        verbClass INTEGER NOT NULL)
                """
            )

            // Copy the data
            database.execSQL(
                """
                    INSERT INTO words_new (id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, verbClass)
                    SELECT id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, verbClass
                        FROM word_table
                    """
            )

            // Remove the old table
            database.execSQL("DROP TABLE word_table")

            // Change the table name to the correct one
            database.execSQL("ALTER TABLE words_new RENAME TO word_table")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE word_table ADD COLUMN gender TEXT NOT NULL DEFAULT ''")

            database.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS words_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        word TEXT NOT NULL, wordIAST TEXT NOT NULL, 
                        meaningEn TEXT NOT NULL, meaningRo TEXT NOT NULL, 
                        gType TEXT NOT NULL, paradigm TEXT NOT NULL, 
                        verbClass INTEGER NOT NULL,
                        gender TEXT NOT NULL)
                """
            )

            // Copy the data
            database.execSQL(
                """
                    INSERT INTO words_new (id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, verbClass, gender)
                    SELECT id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, verbClass, gender FROM word_table
                """
            )

            // Remove the old table
            database.execSQL("DROP TABLE word_table")

            // Change the table name to the correct one
            database.execSQL("ALTER TABLE words_new RENAME TO word_table")
        }
    }

    /**
     * Create tables for different grammatical types
     */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            createTableSubstantives(database)
            createTableWordsPronouns(database)
            createTableWordsNumerals(database)
            createTableWordsVerbs(database)
            createTableWordsOther(database)
        }
    }

    /**
     * Delete words old table
     */
    val MIGRATION_5_6 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                                    DROP TABLE word_table
                                """)
        }
    }

    private fun createTableSubstantives(database: SupportSQLiteDatabase) {
        database.apply {
            execSQL(
                """
                        CREATE TABLE IF NOT EXISTS table_words_substantives (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                            gType TEXT NOT NULL,
                            word TEXT NOT NULL, wordIAST TEXT NOT NULL, meaningEn TEXT NOT NULL, meaningRo TEXT NOT NULL,
                            paradigm TEXT NOT NULL, gender TEXT NOT NULL)
                    """
            )
            execSQL(
                """
                        INSERT INTO table_words_substantives (id, gType, word, wordIAST, meaningEn, meaningRo, gender, paradigm)
                        SELECT id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, gender FROM word_table WHERE gType IN ('noun', 'proper noun', 'adjective')
                    """
            )
        }
    }

    private fun createTableWordsPronouns(database: SupportSQLiteDatabase) {
        database.apply {
            execSQL("""
                            CREATE TABLE IF NOT EXISTS table_words_pronouns (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                                gType TEXT NOT NULL,
                                word TEXT NOT NULL, wordIAST TEXT NOT NULL, meaningEn TEXT NOT NULL, meaningRo TEXT NOT NULL,
                                paradigm TEXT NOT NULL, gender TEXT NOT NULL)
                        """)
            execSQL("""
                            INSERT INTO table_words_pronouns (id, gType, word, wordIAST, meaningEn, meaningRo, paradigm, gender)
                            SELECT id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, gender FROM word_table WHERE gType = 'pronoun'
                        """)
            execSQL("""
                            ALTER TABLE table_words_pronouns
                            ADD number TEXT NOT NULL
                            DEFAULT 'n/a';
                        """)
            execSQL("""
                            ALTER TABLE table_words_pronouns
                            ADD person TEXT NOT NULL
                            DEFAULT 'n/a';
                        """)
            execSQL("""
                            ALTER TABLE table_words_pronouns
                            ADD gCase TEXT NOT NULL
                            DEFAULT 'n/a';
                        """)
        }
    }

    private fun createTableWordsNumerals(database: SupportSQLiteDatabase) {
        database.apply {
            execSQL("""
                            CREATE TABLE IF NOT EXISTS table_words_numerals (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                                gType TEXT NOT NULL,
                                word TEXT NOT NULL, wordIAST TEXT NOT NULL, meaningEn TEXT NOT NULL, meaningRo TEXT NOT NULL,
                                gender TEXT NOT NULL, gCase TEXT NOT NULL)
                        """)
        }
    }

    private fun createTableWordsVerbs(database: SupportSQLiteDatabase) {
        database.apply {
            execSQL("""
                            CREATE TABLE IF NOT EXISTS table_words_verbs (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                                gType TEXT NOT NULL,
                                word TEXT NOT NULL, wordIAST TEXT NOT NULL, meaningEn TEXT NOT NULL, meaningRo TEXT NOT NULL,
                                paradigm TEXT NOT NULL, verbClass INTEGER NOT NULL)
                        """)
            execSQL("""
                            INSERT INTO table_words_verbs (id, gType, word, wordIAST, meaningEn, meaningRo, paradigm, verbClass)
                            SELECT id, word, wordIAST, meaningEn, meaningRo, gType, paradigm, verbClass FROM word_table WHERE gType = 'verb'
                        """)
        }
    }

    private fun createTableWordsOther(database: SupportSQLiteDatabase) {
        database.apply {
            execSQL("""
                            CREATE TABLE IF NOT EXISTS table_words_other (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                                gType TEXT NOT NULL,
                                word TEXT NOT NULL, wordIAST TEXT NOT NULL, meaningEn TEXT NOT NULL, meaningRo TEXT NOT NULL)
                        """)
            execSQL("""
                            INSERT INTO table_words_other (id, gType, word, wordIAST, meaningEn, meaningRo)
                            SELECT id, word, wordIAST, meaningEn, meaningRo, gType FROM word_table WHERE gType NOT IN ('noun', 'proper noun', 'adjective', 'pronoun', 'verb', 'numeral cardinal', 'numeral ordinal')
                        """)
        }
    }

}