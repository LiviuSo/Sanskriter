package com.android.lvicto.words.controller

import android.util.Log
import android.view.View
import com.android.lvicto.R
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.ControllerMvcImpl
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.navigateBack
import com.android.lvicto.db.data.GrammaticalCase
import com.android.lvicto.db.data.GrammaticalNumber
import com.android.lvicto.db.data.GrammaticalPerson
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.declension.adapter.DeclensionEngine
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.activities.WordsActivity
import com.android.lvicto.words.usecase.WordsInsertUseCase
import com.android.lvicto.words.usecase.WordsUpdateUseCase
import com.android.lvicto.words.view.WordDetailsView
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

    override fun onStart() {
        activity.injector.inject(this)
        view?.apply {
            registerListener(this@WordDetailsController)
            buildUI()
        }
    }

    override fun onStop() {
        coroutineScope.coroutineContext.cancelChildren()
        view?.unregisterListener(this)
    }

    override fun onAddWord(view: View, word: Word) {
        coroutineScope.launch {
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
    }

    override fun onEditWord(oldWord: Word?, newWord: Word) {
        coroutineScope.launch {
            oldWord?.let {
                newWord.id = it.id // if null no modification will happen
                val result = wordsUpdateUseCase.updateWord(it, newWord)
                if (result is WordsUpdateUseCase.Result.Success) {
                    dialogManager.showInfoDialog(R.string.dialog_info_message_words_updated) { dialog ->
                        dialog.dismiss()
                        view?.modifyWord(newWord)
                    }
                } else if (result is WordsUpdateUseCase.Result.Failure) {
                    Log.e(LOG_TAG, "Unable to update word: ${result.message}")
                    dialogManager.showErrorDialog(R.string.dialog_error_message_words_update)
                }
            }
        }
    }

    override fun onAddWordError() {
        dialogManager.showErrorDialog(R.string.dialog_error_message_words_added)
    }

    override fun onShowWordGrammar(word: Word?) {
        (activity as WordsActivity).toggleWordGrammarVisibility( if(shouldShowDeclension(word)) word else null )
    }

    // todo create extension
    private fun isParadigmImplemented(word: Word?): Boolean =
        word?.paradigm in arrayListOf(DeclensionEngine.PARADIGM_KANTA, DeclensionEngine.PARADIGM_NADI)

    // todo keep it
    private fun shouldShowDeclension(word: Word?): Boolean =
        isSubstantive(word) && isParadigmImplemented(word)

    // todo create extension
    private fun isSubstantive(oldWord: Word?): Boolean =
        oldWord?.gType in arrayListOf(GrammaticalType.NOUN, GrammaticalType.ADJECTIVE, GrammaticalType.PROPER_NOUN)

    companion object {
        const val LOG_TAG = "add_modify_word_controller"
    }
}