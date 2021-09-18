package com.android.lvicto.common.dialog

import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.conjugation.dialog.ConjugationDialog
import com.android.lvicto.db.entity.Conjugation
import com.android.lvicto.db.entity.Word
import com.android.lvicto.words.dialog.WordDialog

class DialogManager(val activity: AppCompatActivity) {

    fun showErrorDialog(message: String,
                        onRetry: () -> Unit = {}) {
        ErrorDialog(activity, message, onRetry).showDialog()
    }

    fun showConjugationDialog(conjugation: Conjugation,
                              onPositiveAction: (Conjugation) -> Unit = {}) {
        ConjugationDialog(activity, conjugation, onPositiveAction).showDialog()
    }

    fun showWordDialog(word: Word, action: () -> Unit) {
        WordDialog(activity, word, action).showDialog()
    }

}