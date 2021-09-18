package com.android.lvicto.words.activities

import android.os.Bundle
import com.android.lvicto.R
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.words.fragments.WordsFragment

class WordsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.containerWords, WordsFragment())
                .commit()
        }
    }
}