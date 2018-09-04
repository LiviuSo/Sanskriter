package com.android.lvicto.sanskriter.ui.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.activities.SetupActivity
import com.android.lvicto.sanskriter.utils.Constants

abstract class SetupFragment : Fragment() {

    protected lateinit var step: Constants.SetupStep
    protected lateinit var nextStep: Constants.SetupStep
    protected lateinit var ctaSetupLabel: String
    protected lateinit var errorDlgMessage: String
    protected var nextBtnLabel: String = "Next"

    protected lateinit var tvSetupStep1: TextView
    protected lateinit var tvSetupStep2: TextView
    protected lateinit var tvSetupStep3: TextView
    protected lateinit var btnSetup: Button
    protected lateinit var btnSetupNext: Button


    protected val nextBtnOnClickListener: View.OnClickListener = View.OnClickListener {
        if (canContinue()) {
            // move to next Fragment
            moveToNext()
        } else {
            showError()
            btnSetupNext.isEnabled = false
        }
    }

    // todo make them res
    protected val step1Label = "Install the keyboard"
    protected val step2Label = "Enable the keyboard"
    protected val step3Label = "Set the keyboard as default"

    private fun showError() {
        Toast.makeText(activity, errorDlgMessage, Toast.LENGTH_SHORT).show()
    }

    protected fun moveToNext() {
        (activity as SetupActivity).setFragment(nextStep)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_setup, container, false)

        tvSetupStep1 = view.findViewById(R.id.tvStep1)
        tvSetupStep1.text = step1Label
        tvSetupStep2 = view.findViewById(R.id.tvStep2)
        tvSetupStep2.text = step2Label
        tvSetupStep3 = view.findViewById(R.id.tvStep3)
        tvSetupStep3.text = step3Label
        btnSetup = view.findViewById(R.id.setupCta)
        btnSetup.text = ctaSetupLabel
        if(canContinue()) {
            btnSetup.isEnabled = false
        } else {
            btnSetup.setOnClickListener(getOnClickSetupListener())
        }
        btnSetupNext = view.findViewById(R.id.btnNext)
        btnSetupNext.setOnClickListener(nextBtnOnClickListener)
        btnSetupNext.text = nextBtnLabel
        btnSetupNext.isEnabled = canContinue()

        return view
    }

    abstract fun canContinue(): Boolean

    abstract fun getOnClickSetupListener(): View.OnClickListener

}