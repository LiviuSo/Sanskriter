package com.android.lvicto.sanskriter.ui.fragments

import android.view.View
import com.android.lvicto.sanskriter.utils.Constants
import com.android.lvicto.sanskriter.utils.KeyboardHelper
import com.android.lvicto.sanskriter.utils.PreferenceHelper

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
            keyboardName = PreferenceHelper.getKeyboardName()
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