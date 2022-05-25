package com.android.lvicto.sanskriter.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.activities.SandhiActivity
import com.android.lvicto.sanskriter.ui.activities.TestKeyboardActivity
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false).apply {
            this.btnTestDic.setOnClickListener {
            }

            this.btnTestKeyboard.setOnClickListener {
                startActivity(Intent(requireContext(), TestKeyboardActivity::class.java))
            }

            this.btnTestBook2.setOnClickListener {
                findNavController().navigate(R.id.action_from_mainFragment_to_bookActivity)
            }

            this.btnTestSanshi.setOnClickListener {
                startActivity(Intent(requireContext(), SandhiActivity::class.java))
            }
        }
    }

}