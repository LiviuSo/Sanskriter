package com.android.lvicto.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.android.lvicto.R
import com.android.lvicto.common.fragment.BaseFragment
import com.android.lvicto.common.extention.navigate
import kotlinx.android.synthetic.main.fragment_dictionary_test.view.*

class DictionaryTestFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_dictionary_test, container, false).apply {

            this.btnTestWords.setOnClickListener {
                val action = DictionaryTestFragmentDirections.actionDictionaryTestFragmentToWordsFragment(2)
                findNavController().navigate(action)
//                it.navigate(R.id.action_dictionaryTestFragment_to_wordsFragment)
            }

            this.btnDeclensionConstruction.setOnClickListener {
                it.navigate(R.id.action_dictionaryTestFragment_to_declensionFragment)
            }

            this.btnConjugationConstruction.setOnClickListener {
                it.navigate(R.id.action_dictionaryTestFragment_to_conjugationFragment)
            }
        }

}
