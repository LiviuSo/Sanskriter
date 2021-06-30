package com.android.lvicto.common.dialog

import android.app.Activity
import android.app.AlertDialog

abstract class BaseDialog(
    val activity: Activity,
    val message: String
) : AlertDialog(activity) {

    abstract fun setupDialog(builder: Builder): Builder

    private fun createDialog(): AlertDialog = setupDialog(Builder(activity).setMessage(message)).create()

    fun showDialog() {
        createDialog().show()
    }

}