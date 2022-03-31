package com.android.lvicto.common.base

import androidx.fragment.app.Fragment
import com.android.lvicto.dependencyinjection.Injector
import com.android.lvicto.dependencyinjection.composition.ControllerCompositionRoot

open class BaseFragment : Fragment() {

    private val controllerCompositionRoot: ControllerCompositionRoot by lazy {
        ControllerCompositionRoot(activityCompositionRoot = (requireActivity() as BaseActivity).activityCompositionRoot)
    }

    val injector: Injector by lazy {
        Injector(compositionRoot = controllerCompositionRoot)
    }

}