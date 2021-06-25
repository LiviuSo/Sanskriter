package com.android.lvicto.sanskriter

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.android.lvicto.common.dependencyinjection.AppCompositionRoot
import com.android.lvicto.conjugation.activities.ConjugationActivity

class MyApplication : Application() {

    private lateinit var _conjugationAppCompositionRoot: AppCompositionRoot

    companion object {
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        _conjugationAppCompositionRoot = AppCompositionRoot(application)
    }

    // todo abstract class
    private val activityLifecycleCallbacks: ActivityLifecycleCallbacks = object: ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if(activity is ConjugationActivity) {
                activity.setComposition(_conjugationAppCompositionRoot) // inject
            }
        }

        override fun onActivityStarted(activity: Activity) {
            // empty
        }

        override fun onActivityResumed(activity: Activity) {
            // empty
        }

        override fun onActivityPaused(activity: Activity) {
            // empty
        }

        override fun onActivityStopped(activity: Activity) {
            // empty
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            // empty
        }

        override fun onActivityDestroyed(activity: Activity) {
            // empty
        }

    }

}
