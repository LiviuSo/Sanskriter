package com.android.lvicto.common.dialog.new

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.android.lvicto.R

class DialogManager2(val context: Context, private val fragmentManager: FragmentManager) {

    fun showInfoDialog(@StringRes messageId: Int, action: ((OneButtonDialog) -> Unit)? = null) {
        InfoDialog.createInfoDialog(
            context.resources.getString(messageId),
            context.resources.getString(R.string.info_dialog_postive_caption),
            action
        ).show(fragmentManager, "info_dialog")
    }

    fun showErrorDialog(@StringRes messageId: Int) {
        ErrorDialog.createErrorDialog(
            context.resources.getString(messageId),
            context.resources.getString(R.string.error_dialog_postive_caption)
        ).show(fragmentManager, "error_dialog")
    }

}