package com.android.lvicto.common.base

interface ObservableMvc<ListenerType> {
    fun registerListener(listener: ListenerType)
    fun unregisterListener(listener: ListenerType)
}