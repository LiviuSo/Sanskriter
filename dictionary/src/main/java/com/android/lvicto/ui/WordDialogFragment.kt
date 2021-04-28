package com.android.lvicto.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.android.lvicto.R
import com.android.lvicto.db.entity.Word
import com.android.lvicto.util.Constants

class WordDialog(val context: Activity, val word: Word){

    private fun createDialog(): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton(R.string.fire) { dialog, which ->
            if(which == DialogInterface.BUTTON_POSITIVE) {
                dialog.dismiss()
            }
        }.setMessage("$word").setNeutralButton(R.string.edit) { dialog, which ->
            if(which == DialogInterface.BUTTON_NEUTRAL) {
                val intentEdit = Intent(context, AddModifyWordActivity::class.java)
                intentEdit.putExtra(Constants.Dictionary.EXTRA_WORD, word)
                intentEdit.putExtra(
                    Constants.Dictionary.EXTRA_REQUEST_CODE,
                    Constants.Dictionary.CODE_REQUEST_EDIT_WORD
                )
                context.startActivityForResult(intentEdit,
                    Constants.Dictionary.CODE_REQUEST_EDIT_WORD
                )
            }
        }
        return builder.create()
    }

    fun showDialog() {
        createDialog().show()
    }

}