package com.android.lvicto.sanskriter.ui.fragments

import android.view.View
import com.android.lvicto.sanskriter.MyApplication
import com.android.lvicto.sanskriter.utils.Constants
import com.android.lvicto.sanskriter.utils.KeyboardHelper
import com.android.lvicto.sanskritkeyboard.utils.PreferenceHelper

class InstallSetupFragment : SetupFragment() {

    private lateinit var packageName: String

    companion object {
        fun create(): InstallSetupFragment {
            val instance = InstallSetupFragment()
            instance.step = Constants.SetupStep.INSTALL
            instance.nextStep = Constants.SetupStep.ENABLE
            instance.ctaSetupLabel = "Install" // todo make res
            instance.errorDlgMessage = "Keyboard not installed."
            instance.packageName = ""
            return instance
        }
    }

    override fun canContinue(): Boolean {
        if(packageName.isEmpty()) {
            packageName = PreferenceHelper(MyApplication.application.applicationContext).getKeyboardPackage()
        }
        return KeyboardHelper.softInputInstalled(packageName = packageName)
    }

    override fun getOnClickSetupListener() = View.OnClickListener {
        if(!canContinue()) {
            KeyboardHelper.sendToPlayStore(packageName = packageName)
        } else {
            btnSetup.isEnabled = false
        }
        btnSetupNext.isEnabled = true
    }
}