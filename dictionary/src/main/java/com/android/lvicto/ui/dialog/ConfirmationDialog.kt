package com.android.lvicto.ui.dialog

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.db.entity.Conjugation

class ConfirmationDialog(val activity: AppCompatActivity,
                         val conjugation: Conjugation,
                         private val onPositiveAction: (Conjugation) -> Unit) {

    private fun createDialog(): AlertDialog = AlertDialog.Builder(activity).apply {
        this.setMessage("Are you sure you want to delete:\n $conjugation ?")
            .setPositiveButton("OK") { dialog, _ ->
                onPositiveAction.invoke(conjugation)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
    }.create()

    fun showDialog() {
        createDialog().show()
    }
}