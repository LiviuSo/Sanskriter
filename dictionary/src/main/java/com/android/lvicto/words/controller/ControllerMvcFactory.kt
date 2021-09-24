package com.android.lvicto.words.controller

import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.words.controller.WordsController

class ControllerMvcFactory(val activity: BaseActivity) {

    fun getWordsControllerMvc(): WordsController {
        return WordsController(activity)
    }

}
