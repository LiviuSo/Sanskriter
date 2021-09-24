package com.android.lvicto.common.dialog.new

import androidx.fragment.app.DialogFragment
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.dependencyinjection.Injector
import com.android.lvicto.dependencyinjection.composition.ControllerCompositionRoot

open class BaseDialog : DialogFragment() {

    private val controllerCompositionRoot: ControllerCompositionRoot by lazy {
        ControllerCompositionRoot((requireActivity() as BaseActivity).activityCompositionRoot)
    }

    val injector: Injector by lazy {
        Injector(controllerCompositionRoot)
    }

}