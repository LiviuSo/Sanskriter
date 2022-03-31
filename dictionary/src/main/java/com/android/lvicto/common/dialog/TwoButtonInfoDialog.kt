package com.android.lvicto.common.dialog

import com.android.lvicto.R

class TwoButtonInfoDialog(message: String,
                          captionPositive: String,
                          captionNegative: String,
                          actionPositive: (TwoButtonDialog) -> Unit,
                          actionNegative: (TwoButtonDialog) -> Unit)
    : TwoButtonDialog(message = message, captionPositive = captionPositive, captionNegative = captionNegative, actionPositive = actionPositive, actionNegative = actionNegative) {

    override fun getLayout(): Int = R.layout.dialog_info_positive

    override fun getMessageViewId(): Int = R.id.txtMessage

    override fun getCaptionPositiveId(): Int = R.id.btnPositive

    override fun getButtonNegativeId(): Int = R.id.btnNegative

}