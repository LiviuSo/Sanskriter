package com.android.lvicto.words.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.Constants.EXTRA_WORD_EN
import com.android.lvicto.common.Constants.EXTRA_WORD_IAST
import com.android.lvicto.common.base.BaseFragment
import com.android.lvicto.common.factory.ViewMvcFactory
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.common.factory.ControllerMvcFactory
import com.android.lvicto.words.controller.WordsController
import com.android.lvicto.words.view.WordsView

// todo fix bug: if an item is selected then unselected and then scrolled - the item is auto-selected

class WordsFragment : BaseFragment() {

    private lateinit var view: WordsView
    private lateinit var controller: WordsController

    @field:Service
    private lateinit var viewFactory: ViewMvcFactory

    @field:Service
    private lateinit var controllerFactory: ControllerMvcFactory

    private var wordIAST: String? = null
    private var wordEn: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().intent.apply {
            wordIAST = getStringExtra(EXTRA_WORD_IAST) ?: ""
            wordEn = getStringExtra(EXTRA_WORD_EN) ?: ""
        }
    }

    // region lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        injector.inject(this)

        view = viewFactory.getWordsViewMvc(requireActivity() as BaseActivity, container)
        controller = controllerFactory.getWordsControllerMvc()
        controller.bindView(view)
        return view.getRootView()
    }

    override fun onStart() {
        super.onStart()
        controller.onStart(wordIAST, wordEn)
    }

    override fun onStop() {
        super.onStop()
        controller.onStop()
    }
    // endregion

    companion object {
        const val LOG_TAG = "WordsFragment_log"
    }
}