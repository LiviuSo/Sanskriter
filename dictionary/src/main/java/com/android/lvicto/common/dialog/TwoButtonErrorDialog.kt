package com.android.lvicto.common.dialog

import com.android.lvicto.R

class TwoButtonErrorDialog(message: String,
                           captionPositive: String,
                           captionNegative: String,
                           actionRetry: (TwoButtonDialog) -> Unit)
    : TwoButtonDialog(message = message, captionPositive =  captionPositive, captionNegative = captionNegative, actionRetry, { it.dismiss()}) {

    override fun getLayout(): Int = R.layout.dialog_error_retry

    override fun getMessageViewId(): Int = R.id.txtMessage

    override fun getCaptionPositiveId(): Int = R.id.btnPositive

    override fun getButtonNegativeId(): Int = R.id.btnNegative

}