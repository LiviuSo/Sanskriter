package com.android.lvicto.sanskriter.ui.fragments

import android.view.View
import com.android.lvicto.sanskriter.MyApplication
import com.android.lvicto.sanskriter.util.Constants
import com.android.lvicto.sanskriter.util.KeyboardHelper
import com.android.lvicto.sanskriter.util.PreferenceHelper

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
            keyboardName = PreferenceHelper(MyApplication.application.applicationContext).getKeyboardName()
        }
        return KeyboardHelper.isSoftInputEnabled(name = keyboardName)
    }

    override fun getOnClickSetupListener(): View.OnClickListener = View.OnClickListener {
        if(!canContinue()) {
            val activity = this.activity
            if(activity != null) {
                KeyboardHelper.showInputMethodsManager(activity)
            } else {
                // do smth - log or error dialog
            }
            btnSetupNext.isEnabled = true
        } else {
            btnSetup.isEnabled = false
        }
        btnSetupNext.isEnabled = true
    }
}