package com.android.lvicto.words.activities

import android.os.Bundle
import com.android.lvicto.R
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.words.fragments.AddModifyWordFragment

class AddModifyWordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_modify)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.containerAddModify, AddModifyWordFragment())
                .commit()
        }
    }

 }