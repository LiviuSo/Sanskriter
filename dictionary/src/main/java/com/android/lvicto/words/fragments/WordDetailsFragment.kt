package com.android.lvicto.words.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.common.Constants.EXTRA_MODE_CODE
import com.android.lvicto.common.Constants.EXTRA_REQUEST_CODE
import com.android.lvicto.common.Constants.EXTRA_WORD
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.BaseFragment
import com.android.lvicto.common.factory.ControllerFactory
import com.android.lvicto.common.factory.ViewMvcFactory
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.controller.WordDetailsController
import com.android.lvicto.words.view.WordDetailsView
import com.android.lvicto.words.view.WordDetailsViewImpl

class WordDetailsFragment : BaseFragment() {

    private lateinit var wordDetailsView: WordDetailsView
    private lateinit var wordDetailsController: WordDetailsController // todo use interface

    @field:Service
    private lateinit var controllerFactory: ControllerFactory

    @field:Service
    private lateinit var viewFactory: ViewMvcFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            injector.inject(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        wordDetailsController = controllerFactory.getWordDetailsController()

        var word: Word? = null
        var requestCode: Int = -1
        var mode: Int = -1

        arguments?.apply {
            mode = getInt(EXTRA_MODE_CODE)
            requestCode = getInt(EXTRA_REQUEST_CODE)
            word = getParcelable(EXTRA_WORD)
        }
        wordDetailsView = viewFactory.getWordDetailsView(requireActivity() as BaseActivity, container, mode, requestCode, word)
        return (wordDetailsView as WordDetailsViewImpl).getRootView()
    }

    override fun onStart() {
        super.onStart()
        wordDetailsController.bindView(wordDetailsView)
        wordDetailsController.onStart()
    }

    override fun onStop() {
        super.onStop()
        wordDetailsController.onStop()
    }

    companion object {
        @JvmStatic
        fun createBundle(word: Word, codeRequest: Int, mode: Int) = Bundle().apply {
            putParcelable(EXTRA_WORD, word)
            putInt(EXTRA_REQUEST_CODE, codeRequest)
            putInt(EXTRA_MODE_CODE, mode)
        }
    }
}