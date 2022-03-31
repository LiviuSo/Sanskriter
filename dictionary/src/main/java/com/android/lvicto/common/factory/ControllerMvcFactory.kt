package com.android.lvicto.common.factory

import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.words.controller.WordsController

class ControllerMvcFactory(val activity: BaseActivity) {

    fun getWordsControllerMvc(): WordsController {
        return WordsController(activity)
    }

}