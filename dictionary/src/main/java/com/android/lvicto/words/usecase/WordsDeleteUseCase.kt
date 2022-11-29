package com.android.lvicto.words.usecase

import com.android.lvicto.common.Word
import com.android.lvicto.common.concatenate
import com.android.lvicto.db.dao.gtypes.*
import com.android.lvicto.db.data.GrammaticalType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class WordsDeleteUseCase(
    private val substantiveDao: SubstantiveDao,
    private val pronounDao: PronounDao,
    private val verbsDao: VerbDao,
    private val numeralDao: NumeralDao,
    private val otherDao: OtherDao
) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun deleteWords(words: List<Word>): Result = withContext(Dispatchers.IO) {
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
            val substantivesDeferred = async { substantiveDao.deleteSubstantives(substantives) }
            val pronounDeferred = async { pronounDao.deletePronouns(pronouns) }
            val numeralsDeferred = async { numeralDao.deleteNumerals(numerals) }
            val verbsDeferred = async { verbsDao.deleteVerbs(verbs) }
            val otherDeferred = async { otherDao.deleteOthers(others) }

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
