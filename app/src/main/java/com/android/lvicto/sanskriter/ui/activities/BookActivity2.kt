package com.android.lvicto.sanskriter.ui.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.fragments.BookContentsFragment
import com.android.lvicto.sanskriter.ui.fragments.BookPagesFragment

class BookActivity2 : AppCompatActivity(),
        BookContentsFragment.OnFragmentInteractionListener,
        BookPagesFragment.OnFragmentInteractionListener {

    override fun onBookContentsInteraction(uri: Uri) {
        // todo
    }

    override fun onBookPagesInteraction(uri: Uri) {
        // todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book2)

        supportFragmentManager.beginTransaction().add(R.id.fragmentHolder,
                BookContentsFragment.newInstance(), FRAG_BOOK_CONTENTS).commit()

    }

    companion object {
        private const val FRAG_BOOK_CONTENTS = "BookContentsFragment"
        private const val FRAG_BOOK_PAGES = "BookContentsFragment"
    }
}