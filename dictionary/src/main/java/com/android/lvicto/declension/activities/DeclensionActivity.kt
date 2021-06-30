package com.android.lvicto.declension.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.R
import com.android.lvicto.declension.fragment.DeclensionFragment

class DeclensionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_declension)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.containerDeclension, DeclensionFragment())
                .commit()
        }
    }
}