package com.android.lvicto.words.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.common.Constants.EXTRA_WORD
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.BaseFragment
import com.android.lvicto.common.factory.ControllerFactory
import com.android.lvicto.common.factory.ViewMvcFactory
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.controller.WordGrammarController
import com.android.lvicto.words.view.WordGrammarView
import com.android.lvicto.words.view.WordGrammarViewImpl

class WordGrammarFragment : BaseFragment() {

    @field:Service
    private lateinit var controllerFactory: ControllerFactory

    @field:Service
    private lateinit var viewFactory: ViewMvcFactory

    private lateinit var wordGrammarController: WordGrammarController
    private lateinit var wordGrammarView: WordGrammarView
    private lateinit var word: Word


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null) {
            injector.inject(this)
            word = arguments?.getParcelable(EXTRA_WORD) ?: Word()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        wordGrammarController = controllerFactory.getWordGrammarController()
        wordGrammarView = viewFactory.getWordGrammarView(requireActivity() as BaseActivity, container, word)

        return (wordGrammarView as WordGrammarViewImpl).getRootView()
    }

    override fun onStart() {
        super.onStart()
        wordGrammarController.bindView(wordGrammarView)
        wordGrammarController.onStart()
    }

    override fun onStop() {
        super.onStop()
        wordGrammarController.onStop()
    }

    companion object {
        fun newInstance(word: Word) = WordGrammarFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_WORD, word)
            }
        }
    }

}