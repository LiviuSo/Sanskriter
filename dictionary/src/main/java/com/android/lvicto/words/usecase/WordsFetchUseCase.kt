package com.android.lvicto.words.usecase

import com.android.lvicto.common.Word
import com.android.lvicto.common.concatenate
import com.android.lvicto.db.dao.gtypes.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class WordsFetchUseCase(
    private val substantiveDao: SubstantiveDao, // todo remove
    private val pronounDao: PronounDao,
    private val verbsDao: VerbDao,
    private val numeralDao: NumeralDao,
    private val otherDao: OtherDao
) {

    sealed class Result {
        class Success(val words: List<Word>) : Result()
        class Failure(val message: String) : Result()
    }

    suspend fun fetchWords(): Result = withContext(Dispatchers.IO) {
        try {
            val substantivesDeferred = async {
                substantiveDao.getAllSubstantives().map { it.wrap() }
            }
            val pronounsDeferred = async {
                pronounDao.getAllPronouns().map { it.wrap() }
            }
            val numeralsDeferred = async {
                numeralDao.getAllNumerals().map { it.wrap() }
            }
            val verbsDeferred = async {
                verbsDao.getAllVerbs().map { it.wrap() }
            }
            val otherDeferred = async {
                otherDao.getAllOthers().map { it.wrap() }
            }
            Result.Success(concatenate(substantivesDeferred.await(),
                pronounsDeferred.await(),
                numeralsDeferred.await(),
                verbsDeferred.await(),
                otherDeferred.await()).sortedBy {
                    it.wordIAST
            })
        } catch (e: CancellationException) {
            Result.Failure("Cancellation exception")
        } catch (e: Exception) {
            Result.Failure("Unexpected error: ${e.message} ")
        }
    }
}