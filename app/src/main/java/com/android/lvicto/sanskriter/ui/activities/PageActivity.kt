package com.android.lvicto.sanskriter.ui.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import com.android.lvicto.sanskriter.R


class PageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "${getString(R.string.chapter)} xxx" // todo retrieve the chapter name from previous activity
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageTextBook = findViewById<ImageView>(R.id.textBookCut)
        imageTextBook.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ch03_nominal_senteces))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
