package com.android.lvicto.sanskriter.ui.fragments

import android.view.View
import com.android.lvicto.sanskriter.utils.Constants
import com.android.lvicto.sanskriter.utils.KeyboardHelper
import com.android.lvicto.sanskriter.utils.PreferenceHelper

class EnableSetupFragment : SetupFragment() {

    private lateinit var keyboardName: String

    companion object {
        fun create(): EnableSetupFragment {
            val instance = EnableSetupFragment()
            instance.step = Constants.SetupStep.INSTALL
            instance.nextStep = Constants.SetupStep.CHOOSE
            instance.ctaSetupLabel = "Enable" // todo make res
            instance.errorDlgMessage = "Keyboard not enabled."
            instance.keyboardName = ""
            return instance
        }
    }

    override fun canContinue(): Boolean {
        if(keyboardName.isEmpty()) {
            keyboardName = PreferenceHelper.getKeyboardName()
        }
        return KeyboardHelper.isSoftInputEnabled(name = keyboardName)
    }

    override fun getOnClickSetupListener(): View.OnClickListener = View.OnClickListener {
        if(!canContinue()) {
            KeyboardHelper.showInputMethodsManager(SetupFragment@this.activity)
            btnSetupNext.isEnabled = true
        } else {
            btnSetup.isEnabled = false
        }
        btnSetupNext.isEnabled = true
    }
}