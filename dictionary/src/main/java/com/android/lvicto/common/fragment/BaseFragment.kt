package com.android.lvicto.common.fragment

import androidx.fragment.app.Fragment
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.common.dependencyinjection.Injector
import com.android.lvicto.common.dependencyinjection.PresentationCompositionRoot

open class BaseFragment : Fragment() {

    private val presentationCompositionRoot: PresentationCompositionRoot by lazy {
        PresentationCompositionRoot(activityCompositionRoot = (requireActivity() as BaseActivity).activityCompositionRoot)
    }

    val injector: Injector by lazy {
        Injector(compositionRoot = presentationCompositionRoot)
    }

}