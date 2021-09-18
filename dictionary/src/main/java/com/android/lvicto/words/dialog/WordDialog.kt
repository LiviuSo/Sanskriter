package com.android.lvicto.words.dialog

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.R
import com.android.lvicto.common.dialog.BaseDialog
import com.android.lvicto.db.entity.Word

class WordDialog(
    activity: AppCompatActivity,
    val word: Word,
    private val action: () -> Unit
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
                    action.invoke()
                }
            }
    }

}