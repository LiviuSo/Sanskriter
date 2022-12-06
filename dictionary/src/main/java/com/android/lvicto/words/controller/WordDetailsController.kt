package com.android.lvicto.words.controller

import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.lvicto.R
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.ControllerMvcImpl
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.navigateBack
import com.android.lvicto.db.data.GrammaticalCase
import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.data.GrammaticalNumber
import com.android.lvicto.db.data.GrammaticalPerson
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.declension.fragment.DeclensionFragment
import com.android.lvicto.declension.usecase.DeclensionFetchUseCase
import com.android.lvicto.declension.usecase.DeclensionFilterUseCase
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.usecase.WordsInsertUseCase
import com.android.lvicto.words.usecase.WordsUpdateUseCase
import com.android.lvicto.words.view.WordDetailsView
import com.android.lvicto.words.view.WordDetailsViewImpl
import kotlinx.coroutines.*

class WordDetailsController(val activity: BaseActivity)
    : ControllerMvcImpl<WordDetailsView>(), WordDetailsView.Listener {

    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @field:Service
    private lateinit var wordsInsertWordsUseCase: WordsInsertUseCase

    @field:Service
    private lateinit var wordsUpdateUseCase: WordsUpdateUseCase

    @field:Service
    private lateinit var dialogManager: DialogManager

    @field:Service
    private lateinit var declensionFetchUseCase: DeclensionFetchUseCase

    @field:Service
    private lateinit var declensionFilterUseCase: DeclensionFilterUseCase


    override fun onStart() {
        activity.injector.inject(this)
        view?.registerListener(this)
    }

    override fun onStop() {
        view?.unregisterListener(this)
        coroutineScope.coroutineContext.cancelChildren()
    }

    override fun onAddWord(v: View, newWord: Word) {
        coroutineScope.launch {
            addWord(v, newWord)
        }
    }

    override fun onEditWord(oldWord: Word?, newWord: Word) {
        coroutineScope.launch {
            oldWord?.let {
                newWord.id = it.id // if null no modification will happen
                modifyWord(it, newWord) {
                    view?.modifyWord(newWord)
                }
            }
        }
    }

    override fun onAddWordError() {
        Log.e("WordDetailsControllerImpl", "onClickAdd() : No word")
        dialogManager.showErrorDialog(R.string.dialog_error_message_words_added) // todo move to controller
    }

    override fun onDetectDeclension(declension: String) {
        coroutineScope.launch {
            view?.let { v ->
                v.setDeclensions(detectDeclension(declension, v.getOldWord()))
            }
        }
    }

    private suspend fun detectDeclension(keySuffix: String, word: Word): List<Declension> {
        val declensions = arrayListOf<Declension>()
        val filterKeyDeclension = Declension().apply {
            paradigm = word.paradigm
        }
        if(view?.isParadigmImplemented(word) == false) {
            Toast.makeText(activity, "Paradigm not implemented yet.", Toast.LENGTH_SHORT).show()
        } else {
            if (keySuffix.isNotEmpty()) { // if empty return all declensions
                filterKeyDeclension.suffix = getLongestSuffix(keySuffix).ifEmpty {
                    keySuffix
                }
            }
            if (word.gender.abbr.isNotEmpty() && word.gender.abbr != GrammaticalGender.NONE.abbr) {
                filterKeyDeclension.gGender = word.gender
            }

            when (val result = declensionFilterUseCase.filter(filterKeyDeclension)) {
                is DeclensionFilterUseCase.Result.Success -> declensions.addAll(result.declensions)
                is DeclensionFilterUseCase.Result.Failure -> Log.d(DeclensionFragment.LOG_TAG, "Error filtering declensions")
                else -> { /* nothing */ }
            }
        }
        return declensions
    }

    private suspend fun getLongestSuffix(keySuffix: String): String {
        var index = keySuffix.length - 1
        val suffixes = hashSetOf<String>()
        while (index >= 0) {
            val result = declensionFetchUseCase.getSuffixes(keySuffix.substring(index, keySuffix.length))
            if (result is DeclensionFetchUseCase.Result.SuccessSuffixes)
                suffixes.addAll(result.declensions.distinct())
            index--
        }
        return if (suffixes.isEmpty()) {
            ""
        } else {
            suffixes.minByOrNull { it.length } ?: "" }
    }

    private suspend fun modifyWord(oldWord: Word, newWord: Word, onSuccess: () -> Unit) {
        val result = wordsUpdateUseCase.updateWord(oldWord, newWord)
        if (result is WordsUpdateUseCase.Result.Success) {
            dialogManager.showInfoDialog(R.string.dialog_info_message_words_updated) {
                it.dismiss()
                onSuccess.invoke()
            }
        } else if (result is WordsUpdateUseCase.Result.Failure) {
            Log.e(LOG_TAG, "Unable to update word: ${result.message}")
            dialogManager.showErrorDialog(R.string.dialog_error_message_words_update)
        }
    }

    private suspend fun addWord(view: View, word: Word) {
        val result = wordsInsertWordsUseCase.insertWord(Word(
            gType = word.gType,
            wordSa = word.wordSa,
            wordIAST = word.wordIAST,
            meaningEn = word.meaningEn,
            meaningRo = word.meaningRo,
            paradigm = word.paradigm,
            gender = word.gender,
            number = GrammaticalNumber.NONE,
            person = GrammaticalPerson.NONE,
            grammaticalCase = GrammaticalCase.NONE,
            verbClass = word.verbClass))

        if (result is WordsInsertUseCase.Result.Success) {
            dialogManager.showInfoDialog(R.string.dialog_info_message_word_added) {
                it.dismiss()
                view.navigateBack()
            }
        } else if (result is WordsInsertUseCase.Result.Failure) {
            result.message?.let {
                Log.e(LOG_TAG, "Unable to update word: ${result.message}")
                dialogManager.showErrorDialog(R.string.dialog_error_message_words_added)
            }
        }
    }

    companion object {
        const val LOG_TAG = "add_modify_word"
    }
}