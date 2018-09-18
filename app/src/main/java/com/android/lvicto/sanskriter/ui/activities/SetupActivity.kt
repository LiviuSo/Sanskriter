package com.android.lvicto.sanskriter.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.fragments.*
import com.android.lvicto.sanskriter.utils.Constants
import com.android.lvicto.sanskriter.utils.KeyboardHelper
import com.android.lvicto.sanskritkeyboard.utils.PreferenceHelper

/**
 * todo:
 *  Complete
 *      - add keyboard selector on "select" screen
 *      - refactor cu lifecycle arch
 *      - improve ui
 */
class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        setFragment(getCurrentStep())
    }

    private fun getCurrentStep(): Constants.SetupStep {
        val preferenceHelper = PreferenceHelper(applicationContext)
        val keyselcted = preferenceHelper.getKeyboardSelected()
        val keyboardName = preferenceHelper.getKeyboardName()
        val keyboardPackage = preferenceHelper.getKeyboardPackage()
        if(!keyselcted || keyboardName.isEmpty() || keyboardPackage.isEmpty()) {
            return Constants.SetupStep.SELECT
        }
        if (!KeyboardHelper.softInputInstalled(packageName = keyboardPackage)
            && !(keyboardName == Constants.Keyboard.SANSKRIT_KEYS_NAME_1 // tweak - for the local keyboard
                        || keyboardPackage == Constants.Keyboard.SANSKRIT_KEYS_PACKAGE_PACKAGE_NAME_1)) {
            return Constants.SetupStep.INSTALL
        }
        if (!KeyboardHelper.isSoftInputEnabled(name = keyboardName)) {
            return Constants.SetupStep.ENABLE
        }
        if (!KeyboardHelper.isDefaultInputMethod(name = keyboardName)) {
            return Constants.SetupStep.CHOOSE
        }
        return Constants.SetupStep.NONE
    }

    fun setFragment(step: Constants.SetupStep) {
        if(step == Constants.SetupStep.NONE) { // if end of setup, move to main
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        // otherwise move to the next step
        when (step) {
            Constants.SetupStep.SELECT -> {
                val fragment = SelectSetupFragment.create()
                fragmentManager.beginTransaction().replace(R.id.setupFragContainer, fragment).commit()
            }
            Constants.SetupStep.INSTALL -> {
                val fragment = InstallSetupFragment.create()
                fragmentManager.beginTransaction().replace(R.id.setupFragContainer, fragment).commit()
            }
            Constants.SetupStep.ENABLE -> {
                val fragment = EnableSetupFragment.create()
                fragmentManager.beginTransaction().replace(R.id.setupFragContainer, fragment).commit()
            }
            Constants.SetupStep.CHOOSE -> {
                val fragment = ChooseSetupFragment.create()
                fragmentManager.beginTransaction().replace(R.id.setupFragContainer, fragment).commit()
            }
            Constants.SetupStep.DONE -> {
                val fragment = DoneSetupFragment.create()
                fragmentManager.beginTransaction().replace(R.id.setupFragContainer, fragment).commit()
            }
            else -> {
            }
        }
    }
}
