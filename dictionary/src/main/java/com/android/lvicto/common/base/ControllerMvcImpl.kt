package com.android.lvicto.common.base

abstract class ControllerMvcImpl<VIEW_TYPE> : ControllerMvc<VIEW_TYPE> {

    protected var view: VIEW_TYPE? = null

    override fun bindView(view: VIEW_TYPE) {
        this.view = view
    }

}