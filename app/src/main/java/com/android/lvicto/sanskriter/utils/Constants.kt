package com.android.lvicto.sanskriter.utils

object Constants {

    object Keyboard {
        const val SANSKRIT_KEYS_NAME_1 = "CustomKeyboard"
        const val SANSKRIT_KEYS_PACKAGE_PACKAGE_NAME_1 = "com.android.lvicto.sanskritkeyboard"

        const val SANSKRIT_KEYS_NAME_2 = "SimpleIMEServiceSky"
        const val SANSKRIT_KEYS_PACKAGE_PACKAGE_NAME_2 = "sky.sanskrit.myphotokeyboard"

        const val EXTRA_WORD_SA = "word_sa"
        const val EXTRA_WORD_IAST = "word_iast"
        const val EXTRA_WORD_WORD_EN = "word_en"
        const val EXTRA_WORD_RO = "word_ro"
        const val EXTRA_WORD_ID = "word_id"
        const val EXTRA_WORD = "EXTRA_WORD"

        const val REQUEST_CODE_ADD_WORD = 111
        const val REQUEST_CODE_EDIT_WORD = 112

        const val EXTRA_SECTION = "section"
    }

    enum class SetupStep {
        SELECT,
        INSTALL,
        ENABLE,
        CHOOSE,
        DONE,
        NONE,
    }
}