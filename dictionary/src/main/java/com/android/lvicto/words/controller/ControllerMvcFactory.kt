package com.android.lvicto.words.controller

import com.android.lvicto.common.base.BaseActivity

class ControllerMvcFactory(val activity: BaseActivity) {

    fun getWordsControllerMvc(): WordsController {
        return WordsController(activity)
    }

}
