package com.android.lvicto.common.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.IdRes
import com.android.lvicto.common.base.BaseDialog

abstract class OneButtonDialog(private val message: String,
                               private val caption: String,
                               private val action: (OneButtonDialog) -> Unit) : BaseDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = Dialog(requireContext()).apply {
            setContentView(getLayout())
            findViewById<TextView>(getMessageViewId()).text = message
            findViewById<Button>(getPositiveButtonId()).apply {
                text = caption
                setOnClickListener {
                    action.invoke(this@OneButtonDialog)
                }
            }
        }

    @IdRes
    protected abstract fun getPositiveButtonId(): Int

}