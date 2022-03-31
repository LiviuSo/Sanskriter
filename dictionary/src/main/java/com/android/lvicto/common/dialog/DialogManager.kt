package com.android.lvicto.common.dialog

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.android.lvicto.R
import com.android.lvicto.db.entity.Conjugation

class DialogManager(val context: Context, private val fragmentManager: FragmentManager) {

    fun showInfoDialog(@StringRes messageId: Int, action: (OneButtonDialog) -> Unit = { it.dismiss() }) {
        context.resources.apply {
            OneButtonInfoDialog(
                getString(messageId),
                getString(R.string.dialog_caption_okay),
                action
            ).show(fragmentManager, "info_dialog")
        }
    }

    fun showErrorDialog(@StringRes messageId: Int) {
        context.resources.apply {
            OneButtonErrorDialog(
                getString(messageId),
                getString(R.string.dialog_caption_close)
            ) { it.dismiss() }.show(fragmentManager, "error_dialog")
        }
    }

    fun showErrorDialogWithRetry(@StringRes messageId: Int, onRetry: (TwoButtonDialog) -> Unit = {}) {
        showErrorDialogWithRetry(context.resources.getString(messageId), onRetry)
    }

    fun showErrorDialogWithRetry(message: String, onRetry: (TwoButtonDialog) -> Unit = {}) {
        context.resources.apply {
            TwoButtonErrorDialog(
                message,
                getString(R.string.dialog_caption_retry),
                getString(R.string.dialog_caption_close),
                onRetry).show(fragmentManager, "error_dialog_with_retry")
        }
    }

    fun showConjugationDialog(conjugation: Conjugation, action: (Conjugation) -> Unit = {}) {
        val actionPositive: (TwoButtonDialog) -> Unit = {
            action.invoke(conjugation)
            it.dismiss()
        }
        context.resources.apply {
            TwoButtonInfoDialog(
                String.format("%s\n\n%s", getString(R.string.dialog_message_confirmation_delete), conjugation.toString()),
                getString(R.string.dialog_caption_okay),
                getString(R.string.dialog_caption_cancel),
                actionPositive
            ) {
                it.dismiss()
            }.show(fragmentManager, "info_dialog_conjugation")
        }
    }
}