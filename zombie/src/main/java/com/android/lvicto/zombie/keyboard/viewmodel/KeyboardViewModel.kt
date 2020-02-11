package com.android.lvicto.zombie.keyboard.viewmodel

import android.content.Context
import android.widget.Toast
import com.android.lvicto.zombie.keyboard.Injector
import com.android.lvicto.zombie.keyboard.KeyboardType
import com.android.lvicto.zombie.keyboard.activity.SettingsActivity
import com.android.lvicto.zombie.keyboard.service.ZombieInputMethodService
import com.android.lvicto.zombie.keyboard.view.key.CandidatesKeyView
import com.android.lvicto.zombie.keyboard.view.keyboard.CustomKeyboardView

open class KeyboardViewModel(val keyboardView: CustomKeyboardView) {

    val context: Context = keyboardView.context

    val ims: ZombieInputMethodService = Injector.getInstance(context).ims

    fun commitText(s: String) {
        ims.commitText(s)
    }

    fun updateCandidates(keyView: CandidatesKeyView) {
        val candidatesLabels = keyView.getCandidatesLabels()
        if (candidatesLabels.isEmpty()) {
            showDigits()
        }
        val candidatesViews = keyboardView.extraKeyViews
        var lastIndex = 0
        (candidatesLabels.indices).forEach {
            candidatesViews[it].apply {
                this.setText(candidatesLabels[it])
                this.enable(true)
            }
            lastIndex++
        }
        // add place holders for the remaining keyViews
        (lastIndex until candidatesViews.size).forEach {
            candidatesViews[it].apply {
                this.setPlaceholder()
                this.enable(false)
            }
        }
        keyboardView.toggleDigitsKeyView.show(true)
    }

    fun showDigits() {
        keyboardView.apply {
            this.extraKeyViews.forEach {
                it.resetText()
                it.enable(true)
            }
            // hide toggleDigits key
            this.toggleDigitsKeyView.show(false)
        }
    }

    fun resetToggled() { // todo do for all symbol toggle Keys
        keyboardView.symbolKeyView.apply {
            this.setTogglePersistent(false)
            this.setPressedUI(false)
        }
    }

    fun performAction() {
        // todo
        Toast.makeText(context, "Action", Toast.LENGTH_SHORT).show()
    }

    fun completeText(suggestion: String) {
        ims.commitText(suggestion)
    }

    fun updateSuggestions() {
        // todo
    }

    fun removeSuggestions() {
        // todo
    }

    fun deleteCurrentSelection() {
        ims.deleteCurrentSelection()
    }

    fun goToSettings() {
        context.startActivity(SettingsActivity.intent(context))
    }

    fun switchKeyBoard() {
        ims.updateKeyboardType(if (ims.keyboardType.value == KeyboardType.IAST) {
            KeyboardType.SA
        } else {
            KeyboardType.IAST
        })
    }
}