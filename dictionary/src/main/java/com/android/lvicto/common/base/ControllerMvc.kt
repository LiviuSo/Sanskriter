package com.android.lvicto.common.base

interface ControllerMvc<VIEW_TYPE> {

    fun bindView(view: VIEW_TYPE)

    fun onStart()

    fun onStop()

}
