package com.android.lvicto.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.android.lvicto.R
import com.android.lvicto.db.entity.Word
import com.android.lvicto.common.util.Constants

class WordDialog(val context: Activity, val word: Word) {

    private fun createDialog(): Dialog = AlertDialog.Builder(context).apply {
        this
            .setPositiveButton(R.string.fire) { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss()
                }
            }
            .setMessage("$word")
            .setNeutralButton(R.string.edit) { _, which ->
                if (which == DialogInterface.BUTTON_NEUTRAL) {
                    val intentEdit = Intent(context, AddModifyWordActivity::class.java)
                    intentEdit.putExtra(Constants.Dictionary.EXTRA_WORD, word)
                    intentEdit.putExtra(
                        Constants.Dictionary.EXTRA_REQUEST_CODE,
                        Constants.Dictionary.CODE_REQUEST_EDIT_WORD
                    )
                    this@WordDialog.context.startActivityForResult(
                        intentEdit,
                        Constants.Dictionary.CODE_REQUEST_EDIT_WORD
                    )
                }
            }
    }.create()

    fun showDialog() {
        createDialog().show()
    }

}