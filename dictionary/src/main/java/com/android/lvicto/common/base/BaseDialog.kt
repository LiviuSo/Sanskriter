package com.android.lvicto.common.base

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.dependencyinjection.Injector
import com.android.lvicto.dependencyinjection.composition.ControllerCompositionRoot

abstract class BaseDialog : DialogFragment() {

    private val controllerCompositionRoot: ControllerCompositionRoot by lazy {
        ControllerCompositionRoot((requireActivity() as BaseActivity).activityCompositionRoot)
    }

    val injector: Injector by lazy {
        Injector(controllerCompositionRoot)
    }

    @LayoutRes
    protected abstract fun getLayout(): Int

    @IdRes
    protected abstract fun getMessageViewId(): Int

}