package com.android.lvicto.common.base

import com.android.lvicto.common.view.viewinterface.ObservableMvc

open class BaseObservableMvc<LISTENER_TYPE> : BaseViewMvc(), ObservableMvc<LISTENER_TYPE> {

    protected val listeners: HashSet<LISTENER_TYPE> = hashSetOf()

    override fun registerListener(listener: LISTENER_TYPE) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: LISTENER_TYPE) {
        listeners.remove(listener)
    }

}