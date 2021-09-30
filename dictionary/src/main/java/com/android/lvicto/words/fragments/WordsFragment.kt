package com.android.lvicto.words.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.common.constants.Constants.EXTRA_WORD_EN
import com.android.lvicto.common.constants.Constants.EXTRA_WORD_IAST
import com.android.lvicto.common.fragment.BaseFragment
import com.android.lvicto.common.view.factory.ViewMvcFactory
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.controller.ControllerMvcFactory
import com.android.lvicto.words.controller.WordsController
import com.android.lvicto.words.view.WordsViewMvc

/*
bug: if an item is selected then unselected and then scrolled - the item is auto-selected
 */
class WordsFragment : BaseFragment() {

    private lateinit var mViewMvc: WordsViewMvc
    private lateinit var mControllerMvc: WordsController

    @field:Service
    private lateinit var mViewMvcFactory: ViewMvcFactory

    @field:Service
    private lateinit var mControllerMvcFactory: ControllerMvcFactory

    private var mWordIast: String? = null
    private var mWordEn: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWordIast = requireActivity().intent.getStringExtra(EXTRA_WORD_IAST) ?: ""
        mWordEn = requireActivity().intent.getStringExtra(EXTRA_WORD_EN) ?: ""
    }
    // region lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        injector.inject(this)

        mViewMvc = mViewMvcFactory.getWordsViewMvc(requireActivity() as BaseActivity, container)
        mControllerMvc = mControllerMvcFactory.getWordsControllerMvc()
        mControllerMvc.bindView(mViewMvc)
        return mViewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        mControllerMvc.onStart(mWordIast, mWordEn)
    }

    override fun onStop() {
        super.onStop()
        mControllerMvc.onStop()
    }
    // endregion

    companion object {
        const val LOG_TAG = "WordsFragment_log"
    }
}