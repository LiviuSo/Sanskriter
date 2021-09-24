package com.android.lvicto.common.dialog.new

import com.android.lvicto.R

class InfoDialog() : OneButtonDialog() {
    override fun getLayout(): Int {
        return R.layout.dialog_info
    }

    companion object {
        fun createInfoDialog(message: String, caption: String) = InfoDialog().apply {
            addArguments(message = message, caption = caption)
        }
    }

}