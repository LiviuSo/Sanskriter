package com.android.lvicto.ui

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.R
import com.android.lvicto.common.util.Constants
import com.android.lvicto.db.entity.Word
import com.android.lvicto.common.dialog.BaseDialog
import com.android.lvicto.words.activities.AddModifyWordActivity

class WordDialog(
    activity: AppCompatActivity,
    val word: Word
) : BaseDialog(activity, word.toString()) {

    override fun setupDialog(builder: Builder): Builder = builder.apply {
        this
            .setPositiveButton(R.string.fire) { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss()
                }
            }
            .setNeutralButton(R.string.edit) { _, which ->
                if (which == DialogInterface.BUTTON_NEUTRAL) {
                    val intentEdit = Intent(context, AddModifyWordActivity::class.java)
                    intentEdit.putExtra(Constants.Dictionary.EXTRA_WORD, word)
                    intentEdit.putExtra(
                        Constants.Dictionary.EXTRA_REQUEST_CODE,
                        Constants.Dictionary.CODE_REQUEST_EDIT_WORD
                    )
                    this@WordDialog.activity.startActivityForResult(
                        intentEdit,
                        Constants.Dictionary.CODE_REQUEST_EDIT_WORD
                    )
                }
            }
    }

}