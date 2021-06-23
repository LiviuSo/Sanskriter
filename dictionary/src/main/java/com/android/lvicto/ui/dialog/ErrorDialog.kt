package com.android.lvicto.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface

class ErrorDialog(val activity: Activity, private val message: String, private val onRetry: () -> Unit) {

    private fun createDialog(): AlertDialog = AlertDialog.Builder(activity).apply {
        this
            .setMessage(message)
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
    }.create()

    fun showDialog() {
        createDialog().show()
    }
}