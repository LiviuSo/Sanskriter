package com.android.lvicto.words.usecase

import com.android.lvicto.common.WordWrapper
import com.android.lvicto.db.dao.WordDao
import com.android.lvicto.db.dao.gtypes.*
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.db.entity.Word
import com.android.lvicto.db.entity.gtypes.*
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

    suspend fun updateWordPlus(oldWord: WordWrapper, word: WordWrapper): Result = withContext(Dispatchers.IO) {
        try {
            if(oldWord.gType == word.gType) {
                word.selectActionByType({
                    substantiveDao.update(it)
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