package com.android.lvicto.common.base

import android.content.Context
import android.view.View

open class ViewMvcImpl<LISTENER_TYPE> : ViewMvc {

    protected var listeners: HashSet<LISTENER_TYPE> = hashSetOf()

    private lateinit var rootView: View

    override fun getRootView(): View = rootView

    protected fun setRootView(rootView: View) {
        this.rootView = rootView
    }

    protected fun getContext(): Context = getRootView().context

}
