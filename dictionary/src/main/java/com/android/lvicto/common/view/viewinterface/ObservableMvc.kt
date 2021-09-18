package com.android.lvicto.common.view.viewinterface

interface ObservableMvc<ListenerType> {
    fun registerListener(listener: ListenerType)
    fun unregisterListener(listener: ListenerType)
}