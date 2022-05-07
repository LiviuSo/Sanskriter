package com.android.lvicto.words.usecase

import com.android.lvicto.common.WordWrapper
import com.android.lvicto.common.concatenate
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.dao.gtypes.*
import com.android.lvicto.db.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class WordsFilterUseCase(val wordDao: WordDao, // todo remove
                         private val substantiveDao: SubstantiveDao,
                         private val pronounDao: PronounDao,
                         private val verbsDao: VerbDao,
                         private val numeralDao: NumeralDao,
                         private val otherDao: OtherDao
) {

    sealed class Result {
        class Success(val words: List<Word>) : Result()
        class SuccessPlus(val words: List<WordWrapper>) : Result()
        class Failure(val message: String) : Result()
    }

    suspend fun filter(key: String, isPrefix: Boolean): Result = withContext(Dispatchers.IO) {
        try {
            val filter = if (isPrefix) {
                "$key%"
            } else {
                "%$key%"
            }
            val words = wordDao.getWords(filter)
            Result.Success(words)
        } catch (e: Exception) {
            Result.Failure("Unable to filter")
        }
    }

    suspend fun filterPlus(key: String, isPrefix: Boolean): Result = withContext(Dispatchers.IO) {
        try {
            val filter = if (isPrefix) { "$key%" } else { "%$key%" }

            val substantivesDeferred = async {
                substantiveDao.getSubstantives(filter).map { it.wrap() }
            }
            val pronounsDeferred = async {
                pronounDao.getPronouns(filter).map { it.wrap() }
            }
            val numeralsDeferred = async {
                numeralDao.getNumerals(filter).map { it.wrap() }
            }
            val verbsDeferred = async {
                verbsDao.getVerbs(filter).map { it.wrap() }
            }
            val otherDeferred = async {
                otherDao.getOthers(filter).map { it.wrap() }
            }
            Result.SuccessPlus(concatenate(
                substantivesDeferred.await(),
                pronounsDeferred.await(),
                numeralsDeferred.await(),
                verbsDeferred.await(),
                otherDeferred.await()
            ).sortedBy {
                it.wordIAST
            })
        } catch (e: Exception) {
            Result.Failure("Unable to filter")
        }
    }
    
    suspend fun filter(filterPrefix: String, filter: String): Result = withContext(Dispatchers.IO) {
        try {
            val filter1 = "%$filter%"
            val filter2 = "$filterPrefix%" // use prefix

            val words = wordDao.getWords(filter1, filter2)
            Result.Success(words)
        } catch (e: Exception) {
            Result.Failure("")
        }
    }

    suspend fun filterPlus(filterPrefix: String, filter: String): Result = withContext(Dispatchers.IO) {
        try {
            val filter1 = "%$filter%"
            val filter2 = "$filterPrefix%" // use prefix

            val substantivesDeferred = async {
                substantiveDao.getSubstantives(filter1, filter2).map { it.wrap() }
            }
            val pronounsDeferred = async {
                pronounDao.getPronouns(filter1, filter2).map { it.wrap() }
            }
            val numeralsDeferred = async {
                numeralDao.getNumerals(filter1, filter2).map { it.wrap() }
            }
            val verbsDeferred = async {
                verbsDao.getVerbs(filter1, filter2).map { it.wrap() }
            }
            val otherDeferred = async {
                otherDao.getOthers(filter1, filter2).map { it.wrap() }
            }
            Result.SuccessPlus(concatenate(
                substantivesDeferred.await(),
                pronounsDeferred.await(),
                numeralsDeferred.await(),
                verbsDeferred.await(),
                otherDeferred.await()).sortedBy {
                it.wordIAST
            })
        } catch (e: Exception) {
            Result.Failure("")
        }
    }

    suspend fun filter(root: String, paradigm: String, isPrefix: Boolean): Result {
        return withContext(Dispatchers.IO) {
            try {
                val filter = if (isPrefix) { "$root%" } else { "%$root%" }
                val words = wordDao.getNounsAndAdjectives(filter, paradigm)
                Result.Success(words)
            } catch (e: java.lang.Exception) {
                Result.Failure("Unable to filter by paradigm")
            }
        }
    }

    suspend fun filterPlus(root: String, paradigm: String, isPrefix: Boolean): Result {
        return withContext(Dispatchers.IO) {
            try {
                val filter = if (isPrefix) { "$root%" } else { "%$root%" }
                val words = substantiveDao.getSubstantivesByParadigm(filter, paradigm).map {
                    it.wrap()
                }
                Result.SuccessPlus(words)
            } catch (e: java.lang.Exception) {
                Result.Failure("Unable to filter by paradigm")
            }
        }
    }
}