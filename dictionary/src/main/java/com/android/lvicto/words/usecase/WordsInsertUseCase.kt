package com.android.lvicto.words.usecase

import com.android.lvicto.common.WordWrapper
import com.android.lvicto.common.concatenate
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.dao.gtypes.*
import com.android.lvicto.db.data.*
import com.android.lvicto.db.entity.Word
import com.android.lvicto.db.entity.gtypes.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class WordsInsertUseCase(private val wordDao: WordDao, // todo remove
                         private val substantiveDao: SubstantiveDao,
                         private val pronounDao: PronounDao,
                         private val verbsDao: VerbDao,
                         private val numeralDao: NumeralDao,
                         private val otherDao: OtherDao) {

    sealed class Result {
        object Success: Result()
        class Failure(val message: String?) : Result()
    }

    @Deprecated("Use insertWordPlus(Word) instead; will be deleted when migration complete")
    suspend fun insertWord(word: Word): Result = withContext(Dispatchers.IO) {
        try {
            wordDao.insert(word)
            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }
    @Deprecated("Use insertWordPlus(List<Word>) instead; will be deleted when migration complete")
    suspend fun insertWords(words: List<Word>): Result = withContext(Dispatchers.IO) {
        try {
            wordDao.insert(words)
            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }

    suspend fun insertWordPlus(word: WordWrapper): Result = withContext(Dispatchers.IO) {
        try {
            when(word.gType) {
                GrammaticalType.NOUN, GrammaticalType.PROPER_NOUN, GrammaticalType.ADJECTIVE -> {
                    substantiveDao.insert(Substantive(gType = word.gType,
                        word = word.wordSa, wordIAST = word.wordIAST, meaningEn = word.meaningEn, meaningRo = word.meaningRo,
                        paradigm = word.paradigm,
                        gender = word.gender
                    ))
                }
                GrammaticalType.PRONOUN -> {
                    pronounDao.insert(Pronoun(gType = word.gType,
                        word = word.wordSa, wordIAST = word.wordIAST, meaningEn = word.meaningEn, meaningRo = word.meaningRo,
                        paradigm = word.paradigm,
                        gender = word.gender, number = word.number, person = word.person,
                        gCase = word.grammaticalCase
                    ))
                }
                GrammaticalType.NUMERAL_CARDINAL, GrammaticalType.NUMERAL_ORDINAL -> {
                    numeralDao.insert(Numeral(gType = word.gType,
                        word = word.wordSa, wordIAST = word.wordIAST, meaningEn = word.meaningEn, meaningRo = word.meaningRo,
                        gender = word.gender,
                        gCase = word.grammaticalCase
                    ))
                }
                GrammaticalType.VERB -> {
                    verbsDao.insert(Verb(gType = word.gType,
                        word = word.wordSa, wordIAST = word.wordIAST, meaningEn = word.meaningEn, meaningRo = word.meaningRo,
                        paradigm = word.paradigm,
                        verbClass = word.verbClass
                    ))
                }
                else -> {
                    otherDao.insert(Other(gType = word.gType,
                        word = word.wordSa, wordIAST = word.wordIAST, meaningEn = word.meaningEn, meaningRo =word. meaningRo
                    ))
                }
            }

            // todo remove after the migration is complete
            wordDao.insert(Word(word = word.wordSa, wordIAST = word.wordIAST, meaningEn = word.meaningEn, meaningRo = word.meaningRo,
                gType = word.gType,
                paradigm = word.paradigm,
                verbClass = word.verbClass,
                gender = word.gender
            ))

            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }

    suspend fun insertWordsPlus(words: List<WordWrapper>): Result  = withContext(Dispatchers.IO) {
        val wordsByType = words.groupBy { it.gType }
        val verbs = (wordsByType[GrammaticalType.VERB]?: listOf()).map { it.toVerb() }
        val pronouns = (wordsByType[GrammaticalType.PRONOUN]?: listOf()).map { it.toPronoun() }
        val numerals = concatenate(wordsByType[GrammaticalType.NUMERAL_CARDINAL] ?: listOf(),
            wordsByType[GrammaticalType.NUMERAL_ORDINAL] ?: listOf()).map { it.toNumeral() }
        val substantives = concatenate(wordsByType[GrammaticalType.NOUN]?: listOf(),
            wordsByType[GrammaticalType.PROPER_NOUN]?: listOf(),
            wordsByType[GrammaticalType.ADJECTIVE]?: listOf()).map { it.toSubstantive() }
        val others = concatenate(wordsByType[GrammaticalType.ADVERB]?: listOf(),
            wordsByType[GrammaticalType.INTERJECTION]?: listOf(),
            wordsByType[GrammaticalType.PREPOSITION]?: listOf(),
            wordsByType[GrammaticalType.SUFFIX]?: listOf(),
            wordsByType[GrammaticalType.PREFIX]?: listOf(),
            wordsByType[GrammaticalType.OTHER]?: listOf()).map { it.toOther() }

        try {
            val substantivesDeferred = async { substantiveDao.insert(substantives) }
            val pronounDeferred = async { pronounDao.insert(pronouns) }
            val numeralsDeferred = async { numeralDao.insert(numerals) }
            val verbsDeferred = async { verbsDao.insert(verbs) }
            val otherDeferred = async { otherDao.insert(others) }

            substantivesDeferred.await()
            pronounDeferred.await()
            numeralsDeferred.await()
            verbsDeferred.await()
            otherDeferred.await()

            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }

}
