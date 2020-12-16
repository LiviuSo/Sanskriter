package com.android.lvicto.sanskriter.util

object Constants {

    object Keyboard {
        const val SANSKRIT_KEYS_NAME_1 = "SanskritKeyboardIms"
        const val SANSKRIT_KEYS_PACKAGE_PACKAGE_NAME_1 = "com.lvicto.skeyboard.service"

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