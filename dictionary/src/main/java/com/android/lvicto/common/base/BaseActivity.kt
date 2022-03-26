package com.android.lvicto.common.base

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.dependencyinjection.composition.ActivityCompositionRoot
import com.android.lvicto.dependencyinjection.composition.AppCompositionRoot
import com.android.lvicto.dependencyinjection.Injector
import com.android.lvicto.dependencyinjection.composition.ControllerCompositionRoot

open class BaseActivity : AppCompatActivity() {

    lateinit var appCompositionRoot: AppCompositionRoot
        private set
    lateinit var activityCompositionRoot: ActivityCompositionRoot
        private set
    lateinit var controllerCompositionRoot: ControllerCompositionRoot
        private set
    lateinit var injector: Injector
        private set

    fun setComposition(root: AppCompositionRoot) {
        appCompositionRoot = root
        activityCompositionRoot = ActivityCompositionRoot(this, appCompositionRoot)
        controllerCompositionRoot = ControllerCompositionRoot(activityCompositionRoot)
        injector = Injector(controllerCompositionRoot)
        Log.d("debconj", "injector initialized")
    }

}
