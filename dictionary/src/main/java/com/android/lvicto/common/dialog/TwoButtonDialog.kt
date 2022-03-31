package com.android.lvicto.common.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.IdRes
import com.android.lvicto.common.base.BaseDialog

abstract class TwoButtonDialog(private val message: String,
                               private val captionPositive: String,
                               private val captionNegative: String,
                               private val actionPositive: (TwoButtonDialog) -> Unit,
                               private val actionNegative: (TwoButtonDialog) -> Unit) : BaseDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            this.setContentView(getLayout())
            findViewById<TextView>(getMessageViewId()).text = message
            findViewById<Button>(getButtonNegativeId()).apply {
                text = captionNegative
                setOnClickListener {
                    actionNegative.invoke(this@TwoButtonDialog)
                }
            }
            findViewById<Button>(getCaptionPositiveId()).apply {
                text = captionPositive
                setOnClickListener {
                    actionPositive.invoke(this@TwoButtonDialog)
                }
            }
        }
    }

    @IdRes
    protected abstract fun getCaptionPositiveId(): Int

    @IdRes
    protected abstract fun getButtonNegativeId(): Int

}