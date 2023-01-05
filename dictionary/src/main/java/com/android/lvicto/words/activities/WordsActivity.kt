package com.android.lvicto.words.activities

import android.os.Bundle
import com.android.lvicto.R
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.words.fragments.WordGrammarFragment

class WordsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words)
    }

    fun toggleWordGrammarVisibility(word: Word?) {
        supportFragmentManager.beginTransaction().apply {
            if (word != null)
                add(R.id.containerWordGrammar, WordGrammarFragment.newInstance(word)).commit()
            else
                supportFragmentManager.findFragmentById(R.id.containerWordGrammar)?.apply {
                    remove(this).commit()
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        toggleWordGrammarVisibility(null)
    }

}