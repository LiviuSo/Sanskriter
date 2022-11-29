package com.android.lvicto.common.base

import android.content.Context
import android.view.View
import com.android.lvicto.common.view.viewinterface.ViewMvc

open class ViewMvcImpl : ViewMvc {

    private lateinit var rootView: View

    override fun getRootView(): View = rootView

    protected fun setRootView(rootView: View) {
        this.rootView = rootView
    }

    protected fun getContext(): Context = getRootView().context

}
