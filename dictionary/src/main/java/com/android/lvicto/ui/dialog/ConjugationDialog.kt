package com.android.lvicto.ui.dialog

import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.db.entity.Conjugation

class ConjugationDialog(
    activity: AppCompatActivity,
    val conjugation: Conjugation,
    private val onPositiveAction: (Conjugation) -> Unit
) : BaseDialog(activity, "Are you sure you want to delete:\n $conjugation ?") {

    override fun setupDialog(builder: Builder): Builder {
        builder
            .setPositiveButton("OK") { dialog, _ ->
                onPositiveAction.invoke(conjugation)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        return builder
    }

}