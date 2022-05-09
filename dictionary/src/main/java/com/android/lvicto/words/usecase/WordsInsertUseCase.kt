package com.android.lvicto.words.usecase

import com.android.lvicto.common.Word
import com.android.lvicto.common.concatenate
import com.android.lvicto.db.dao.gtypes.*
import com.android.lvicto.db.data.GrammaticalType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class WordsInsertUseCase(
    private val substantiveDao: SubstantiveDao, // todo remove
    private val pronounDao: PronounDao,
    private val verbsDao: VerbDao,
    private val numeralDao: NumeralDao,
    private val otherDao: OtherDao
) {

    sealed class Result {
        object Success: Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun insertWord(word: Word): Result = withContext(Dispatchers.IO) {
        try {
            word.selectActionByType({
                substantiveDao.insert(it)
            }, {
                pronounDao.insert(it)
            }, {
                verbsDao.insert(it)
            }, {
                numeralDao.insert(it)
            }, {
                otherDao.insert(it)
            })

            Result.Success
        } catch (e: Exception) {
            Result.Failure(e.message)
        }
    }

    suspend fun insertWords(words: List<Word>): Result  = withContext(Dispatchers.IO) {
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
