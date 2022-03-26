package com.android.lvicto.common.base

import androidx.fragment.app.DialogFragment
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.dependencyinjection.Injector
import com.android.lvicto.dependencyinjection.composition.ControllerCompositionRoot

open class BaseDialog2 : DialogFragment() {

    private val controllerCompositionRoot: ControllerCompositionRoot by lazy {
        ControllerCompositionRoot((requireActivity() as BaseActivity).activityCompositionRoot)
    }

    val injector: Injector by lazy {
        Injector(controllerCompositionRoot)
    }

}