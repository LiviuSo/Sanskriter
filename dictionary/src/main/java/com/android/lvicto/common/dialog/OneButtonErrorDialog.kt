package com.android.lvicto.common.dialog

import com.android.lvicto.R

class OneButtonErrorDialog(message: String, caption: String, action: (OneButtonDialog) -> Unit)
    : OneButtonDialog(message, caption, action) {

    override fun getLayout(): Int = R.layout.dialog_error

    override fun getMessageViewId(): Int = R.id.txtMessage

    override fun getPositiveButtonId(): Int = R.id.btnPositive

}