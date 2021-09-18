package com.android.lvicto.common.eventbus

import com.android.lvicto.common.view.viewinterface.ObservableMvc

class ResultEventBus : ObservableMvc<ResultEventBus.Listener> {
    interface Listener {
        fun onEventReceived(event: Any)
    }

    private val listeners: HashSet<Listener> = hashSetOf()

    // todo create a base class
    override fun registerListener(listener: Listener) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun postEvent(event: Any) {
        for(listener in listeners) {
            listener.onEventReceived(event)
        }
    }

}