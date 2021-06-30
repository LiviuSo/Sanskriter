package com.android.lvicto.ui.dialog

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity

class ErrorDialog(
    activity: AppCompatActivity,
    message: String,
    private val onRetry: () -> Unit
) : BaseDialog(activity, message) {

    override fun setupDialog(builder: Builder): Builder = builder.apply {
        this
            .setPositiveButton("Close") { dialog, which ->
                if(which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss()
                }
            }.setNegativeButton("Retry") { dialog, which ->
                if(which == DialogInterface.BUTTON_NEGATIVE) {
                    onRetry.invoke()
                    dialog.dismiss()
                }
            }
    }

}