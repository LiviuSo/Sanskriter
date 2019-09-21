package com.android.lvicto.sanskriter.ui.fragments

import android.view.View
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.util.Constants
import com.android.lvicto.sanskriter.util.KeyboardHelper
import com.android.lvicto.sanskriter.util.PreferenceHelper

class ChooseSetupFragment : SetupFragment() {

    private lateinit var keyboardName: String

    companion object {
        fun create(): ChooseSetupFragment {
            val instance = ChooseSetupFragment()
            instance.step = Constants.SetupStep.CHOOSE
            instance.nextStep = Constants.SetupStep.DONE
            instance.ctaSetupLabel = "Choose" // todo make res
            instance.errorDlgMessage = "Keyboard not set as default."
            instance.keyboardName = ""

            return instance
        }
    }

    override fun canContinue(): Boolean {
        if(keyboardName.isEmpty()) {
            keyboardName = PreferenceHelper(application.applicationContext).getKeyboardName()
        }
        return KeyboardHelper.isDefaultInputMethod(name = keyboardName)
    }

    override fun getOnClickSetupListener(): View.OnClickListener {
        return View.OnClickListener {
            if(!canContinue()) {
                KeyboardHelper.showSoftInputMethodsSelector()
            } else {
                btnSetup.isEnabled = false
            }
            btnSetupNext.isEnabled = true
        }
    }
}