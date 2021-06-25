package com.android.lvicto.sanskriter.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.data.BookContent
import com.android.lvicto.sanskriter.data.BookSection
import com.android.lvicto.ui.AddDeclensionActivity
import com.android.lvicto.conjugation.activities.ConjugationActivity
import com.android.lvicto.ui.DictionaryActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTestDic.setOnClickListener {
            startActivity(Intent(this@MainActivity, DictionaryActivity::class.java))
        }

        btnTestKeyboard.setOnClickListener {
            startActivity(Intent(this@MainActivity, TestKeyboardActivity::class.java))
        }

        btnTestBook2.setOnClickListener {
            startActivity(Intent(this@MainActivity, BookActivity::class.java))
        }

        btnTestSanshi.setOnClickListener {
            startActivity(Intent(this@MainActivity, SandhiActivity::class.java))
        }

        btnDeclensionConstruction.setOnClickListener {
            startActivity(Intent(this, AddDeclensionActivity::class.java))
        }

        btnConjugationConstruction.setOnClickListener {
            startActivity(Intent(this, ConjugationActivity::class.java))
        }

        // debug
        Log.d(LOG_TAG, writeBookContents(this))
    }


    // debug
    private fun readBookContents(context: Context): BookContent {
        val gson = Gson()
        val inputStreamReader = InputStreamReader(context.assets.open("json/coulson_contents.json"))
        val stringBuilder = StringBuilder()
        inputStreamReader.forEachLine {
            stringBuilder.append(it)
        }
        return gson.fromJson(stringBuilder.toString(), BookContent::class.java)
    }

    // debug
    private fun writeBookContents(context: Context): String {
        val sections = sortedMapOf(
                0 to arrayListOf(
                        BookSection("The nāgarī script",
                                arrayListOf("sections/relativitate.png",
                                        "sections/sexy-butt.png",
                                        "sobi-nike.png")),
                        BookSection("Vowels, anusvara, visarga",
                                arrayListOf("","")),
                        BookSection("Consonants, stops, semivowels, sibilants, h",
                                arrayListOf("","")),
                        BookSection("Conjunct consonants",
                                arrayListOf("","")),
                        BookSection("Other signs",
                                arrayListOf("","")),
                        BookSection("Numerals",
                                arrayListOf("","")),
                        BookSection("Transliteration",
                                arrayListOf("","")),
                        BookSection("Prosody",
                                arrayListOf("","")),
                        BookSection("List of conjunct consonants",
                                arrayListOf("","")))
        )
        val bookContent = BookContent("Coulson",
                1,
                sections)
        return Gson().toJson(bookContent)
    }

}
