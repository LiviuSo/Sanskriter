package com.android.lvicto.common.view

open class BaseViewMvs<LISTENER_TYPE> {

    protected val listeners: HashSet<LISTENER_TYPE> = hashSetOf()

    fun registerListener(listener: LISTENER_TYPE) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: LISTENER_TYPE) {
        listeners.remove(listener)
    }

}
