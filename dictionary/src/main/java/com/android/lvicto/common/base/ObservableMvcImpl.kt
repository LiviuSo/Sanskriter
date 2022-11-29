package com.android.lvicto.common.base

import com.android.lvicto.common.view.viewinterface.ObservableMvc

open class ObservableMvcImpl<LISTENER_TYPE> : ViewMvcImpl(),
    ObservableMvc<LISTENER_TYPE> {

    protected val listeners: HashSet<LISTENER_TYPE> = hashSetOf()

    override fun registerListener(listener: LISTENER_TYPE) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: LISTENER_TYPE) {
        listeners.remove(listener)
    }

}