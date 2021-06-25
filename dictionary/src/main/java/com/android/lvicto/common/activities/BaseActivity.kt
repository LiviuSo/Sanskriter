package com.android.lvicto.common.activities

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.common.dependencyinjection.ActivityComponent
import com.android.lvicto.common.dependencyinjection.AppCompositionRoot
import com.android.lvicto.common.dependencyinjection.Injector
import com.android.lvicto.common.dependencyinjection.PresentationCompositionRoot

open class BaseActivity : AppCompatActivity() {

    lateinit var appCompositionRoot: AppCompositionRoot
        private set
    lateinit var activityCompositionRoot: ActivityComponent
        private set
    lateinit var presentationCompositionRoot: PresentationCompositionRoot
        private set
    lateinit var injector: Injector
        private set

    fun setComposition(root: AppCompositionRoot) {
        appCompositionRoot = root
        activityCompositionRoot = ActivityComponent(this, appCompositionRoot)
        presentationCompositionRoot = PresentationCompositionRoot(activityCompositionRoot)
        injector = Injector(presentationCompositionRoot)
        Log.d("debconj", "injector initialized")
    }


}
