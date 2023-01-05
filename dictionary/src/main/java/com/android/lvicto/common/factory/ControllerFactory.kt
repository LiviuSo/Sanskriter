package com.android.lvicto.common.factory

import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.words.controller.WordDetailsController
import com.android.lvicto.words.controller.WordGrammarController
import com.android.lvicto.words.controller.WordsController

class ControllerFactory(val activity: BaseActivity) {

    fun getWordsController(): WordsController = WordsController(activity)

    fun getWordDetailsController(): WordDetailsController = WordDetailsController(activity)

    fun getWordGrammarController(): WordGrammarController = WordGrammarController(activity)
}