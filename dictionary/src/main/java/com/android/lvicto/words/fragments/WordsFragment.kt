package com.android.lvicto.words.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.common.activities.BaseActivity
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
        mControllerMvc.onStart()
        Log.d(LOG_TAG, "WordsFragment onStart()")
    }

    override fun onStop() {
        super.onStop()
        mControllerMvc.onStop()
        Log.d(LOG_TAG, "WordsFragment onStop()")
    }
    // endregion

    companion object {
        const val LOG_TAG = "WordsFragment_log"
    }
}