package com.android.lvicto.common.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.lvicto.R
import com.android.lvicto.conjugation.activities.ConjugationActivity
import com.android.lvicto.ui.AddDeclensionActivity
import com.android.lvicto.ui.WordsActivity
import kotlinx.android.synthetic.main.activity_dictionary.*

class DictionaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        btnTestWords.setOnClickListener {
            startActivity(Intent(this, WordsActivity::class.java))
        }

        btnDeclensionConstruction.setOnClickListener {
            startActivity(Intent(this, AddDeclensionActivity::class.java))
        }

        btnConjugationConstruction.setOnClickListener {
            startActivity(Intent(this, ConjugationActivity::class.java))
        }
    }
}