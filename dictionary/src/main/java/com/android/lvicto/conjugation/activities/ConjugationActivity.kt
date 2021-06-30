package com.android.lvicto.conjugation.activities

import android.os.Bundle
import android.util.Log
import com.android.lvicto.R
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.conjugation.fragment.ConjugationFragment

class ConjugationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conjugation)

        if(savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.conjugationContainer, ConjugationFragment())
                .commit()
        }
    }

}