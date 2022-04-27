package com.android.lvicto.words.usecase

import com.android.lvicto.common.WordWrapper
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.dao.gtypes.*
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.db.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class WordsUpdateUseCase(val wordDao: WordDao, // todo remove
                         private val substantiveDao: SubstantiveDao,
                         private val pronounDao: PronounDao,
                         private val verbsDao: VerbDao,
                         private val numeralDao: NumeralDao,
                         private val otherDao: OtherDao) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun updateWord(word: Word): Result = withContext(Dispatchers.IO) {
        try {
            wordDao.update(word)
            Result.Success
        } catch (e: Exception) {
            Result.Failure("Unable to update word")
        }
    }

    suspend fun updateWordPlus(word: WordWrapper): Result = withContext(Dispatchers.IO) {
        try {
            when(word.gType) {
                GrammaticalType.NOUN, GrammaticalType.PROPER_NOUN, GrammaticalType.ADJECTIVE -> {
                    substantiveDao.update(word.toSubstantive())
                }
                GrammaticalType.PRONOUN -> {
                    pronounDao.update(word.toPronoun())
                }
                GrammaticalType.VERB -> {
                    verbsDao.update(word.toVerb())
                }
                GrammaticalType.NUMERAL_CARDINAL, GrammaticalType.NUMERAL_ORDINAL -> {
                    numeralDao.insert(word.toNumeral())
                }
                else -> {
                    otherDao.insert(word.toOther())
                }
            }
            Result.Success
        } catch (e: Exception) {
            Result.Failure("Unable to update word")
        }
    }
}