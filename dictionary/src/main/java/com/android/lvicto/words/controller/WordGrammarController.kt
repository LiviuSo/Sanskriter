package com.android.lvicto.words.controller

import android.util.Log
import com.android.lvicto.common.Constants.EMPTY_STRING
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.ControllerMvcImpl
import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.declension.fragment.DeclensionFragment
import com.android.lvicto.declension.usecase.DeclensionFetchUseCase
import com.android.lvicto.declension.usecase.DeclensionFilterUseCase
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.view.WordGrammarView
import kotlinx.coroutines.*

class WordGrammarController(val activity: BaseActivity)
    : ControllerMvcImpl<WordGrammarView>(), WordGrammarView.Listener {

    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @field:Service
    private lateinit var declensionFetchUseCase: DeclensionFetchUseCase

    @field:Service
    private lateinit var declensionFilterUseCase: DeclensionFilterUseCase

    override fun onStart() {
        activity.injector.inject(this)
        view?.registerListener(this)
    }

    override fun onStop() {
        coroutineScope.coroutineContext.cancelChildren()
        view?.unregisterListener(this)
    }

    override fun onDeclensionSearchKeyChanged(ending: String, word: Word) {
        coroutineScope.launch {
            view?.setDeclensions(detectDeclension(ending, word))
        }
    }

    private suspend fun detectDeclension(keySuffix: String, word: Word): List<Declension> {
        val declensions = arrayListOf<Declension>()
        val filterKeyDeclension = Declension().apply {
            paradigm = word.paradigm
        }

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
            EMPTY_STRING
        } else {
            suffixes.filter { it.length >= keySuffix.length }.maxByOrNull { it.length } ?: EMPTY_STRING }
    }
}