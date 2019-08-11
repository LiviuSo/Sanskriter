package com.android.lvicto.sanskriter.utils

object Constants {

    object Keyboard {
        const val SANSKRIT_KEYS_NAME_1 = "CustomKeyboard"
        const val SANSKRIT_KEYS_PACKAGE_PACKAGE_NAME_1 = "com.android.lvicto.sanskritkeyboard"

        const val SANSKRIT_KEYS_NAME_2 = "SimpleIMEServiceSky"
        const val SANSKRIT_KEYS_PACKAGE_PACKAGE_NAME_2 = "sky.sanskrit.myphotokeyboard"

        const val EXTRA_SECTION = "section"
        const val EXTRA_CONTENT = "book_content"
        const val EXTRA_PAGE_TITLE = "page_title"
        const val EXTRA_PAGE_ASSET = "page_asset"
        const val EXTRA_ZOOM_BUNDLE = "extra_zoom_bundle"
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