package com.android.lvicto.sanskriter.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.fragments.GenerateDeclensionFragment
import com.android.lvicto.sanskriter.ui.fragments.GetDeclensionFragment
import com.android.lvicto.sanskriter.util.Constants.Keyboard.LOG_TAG
import kotlinx.android.synthetic.main.activity_declension.*

class DeclensionActivity : AppCompatActivity() {

    private var titleGenerateDeclension: String = ""
    private var titleGetDeclension: String = ""
    private val getDeclensionFragment = GetDeclensionFragment()
    private val generateDeclensionFragment = GenerateDeclensionFragment()
    private var fragment: Fragment = getDeclensionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_declension)

        titleGetDeclension = resources.getString(R.string.get_declension_title)
        titleGenerateDeclension = resources.getString(R.string.generate_declension_title)

        toolbar.title = titleGetDeclension
        setSupportActionBar(toolbar)

        swapFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.declension, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_swap -> {
                swapFragment()
                true
            }
             else -> {
                 false
             }
        }
    }

    private fun swapFragment() {
        fragment = when {
            fragment === generateDeclensionFragment -> {
                toolbar.title = titleGetDeclension
                getDeclensionFragment
            }
            fragment === getDeclensionFragment -> {
                toolbar.title = titleGenerateDeclension
                generateDeclensionFragment
            }
            else -> {
                toolbar.title = titleGetDeclension
                getDeclensionFragment
            }
        }
        supportFragmentManager.beginTransaction()
                .replace(fragmentHolder.id, fragment)
                .commit()
    }
}
