package com.android.lvicto.words.usecase

import com.android.lvicto.common.Word
import com.android.lvicto.db.dao.gtypes.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class WordsUpdateUseCase(
    private val substantiveDao: SubstantiveDao, // todo remove
    private val pronounDao: PronounDao,
    private val verbsDao: VerbDao,
    private val numeralDao: NumeralDao,
    private val otherDao: OtherDao
) {

    sealed class Result {
        object Success : Result()
        class Failure(val message: String?) : Result()
    }

    suspend fun updateWord(oldWord: Word, word: Word): Result = withContext(Dispatchers.IO) {
        try {
            if(oldWord.gType == word.gType) {
                word.selectActionByType({
                    substantiveDao.update(it.id, it.gType.denom, it.word, it.wordIAST, it.meaningEn, it.meaningRo, it.paradigm, it.gender.abbr)
                }, {
                    pronounDao.update(it)
                }, {
                    verbsDao.update(it)
                }, {
                    numeralDao.update(it)
                }, {
                    otherDao.update(it)
                })
            } else {
                oldWord.selectActionByType({
                    substantiveDao.deleteSubstantives(arrayListOf(it))
                }, {
                    pronounDao.deletePronouns(arrayListOf(it))
                }, {
                    verbsDao.deleteVerbs(arrayListOf(it))
                }, {
                    numeralDao.deleteNumerals(arrayListOf(it))
                }, {
                    otherDao.deleteOthers(arrayListOf(it))
                })

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
            }
            Result.Success
        } catch (e: Exception) {
            Result.Failure("Unable to update word")
        }
    }
}