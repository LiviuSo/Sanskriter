package com.android.lvicto.common.base

import android.content.Context
import android.view.View
import com.android.lvicto.common.view.viewinterface.ViewMvc

open class BaseViewMvc : ViewMvc {

    private lateinit var mRootView: View

    override fun getRootView(): View = mRootView

    protected fun setRootView(rootView: View) {
        mRootView = rootView
    }

    protected fun getContext(): Context = getRootView().context

}
