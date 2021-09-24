package com.android.lvicto.common.dialog.new

import com.android.lvicto.R

class ErrorDialog : OneButtonDialog() {
    override fun getLayout(): Int {
        return R.layout.dialog_error
    }

    companion object {
        fun createErrorDialog(message: String, caption: String) = ErrorDialog().apply {
            addArguments(message, caption)
        }
    }

}