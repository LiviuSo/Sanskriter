package com.android.lvicto.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.R
import com.android.lvicto.common.adapter.WordsAdapter
import com.android.lvicto.common.db.data.Words
import com.android.lvicto.common.db.entity.Word
import com.android.lvicto.common.util.Constants.Dictionary.CODE_REQUEST_ADD_WORD
import com.android.lvicto.common.util.Constants.Dictionary.CODE_REQUEST_EDIT_WORD
import com.android.lvicto.common.util.Constants.Dictionary.EXTRA_REQUEST_CODE
import com.android.lvicto.common.util.Constants.Dictionary.EXTRA_WORD
import com.android.lvicto.common.util.Constants.Dictionary.EXTRA_WORD_EN
import com.android.lvicto.common.util.Constants.Dictionary.EXTRA_WORD_IAST
import com.android.lvicto.common.util.Constants.Dictionary.EXTRA_WORD_RESULT
import com.android.lvicto.common.util.Constants.Dictionary.FILENAME_WORDS
import com.android.lvicto.common.util.Constants.Dictionary.PICKFILE_RESULT_CODE
import com.android.lvicto.common.util.Utils.hideSoftKeyboard
import com.android.lvicto.common.util.export
import com.android.lvicto.common.util.openFilePicker
import com.android.lvicto.viewmodel.WordsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.layout_all_words.*
import kotlinx.android.synthetic.main.search_bar.*


class DictionaryActivity : AppCompatActivity() {

    private lateinit var viewModel: WordsViewModel
    private lateinit var wordsAdapter: WordsAdapter

    private lateinit var llRemoveCancel: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var llSearch: LinearLayout
    private lateinit var editSearchEnDic: EditText
    private lateinit var editSearchIastDic: EditText
    private lateinit var ibSearchClose: ImageButton
    private lateinit var fab: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)
        initUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dictionary, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemImport -> {
                Log.d(LOG_TAG, "R.id.menuItemImport")
                openFilePicker(PICKFILE_RESULT_CODE)
                true
            }
            R.id.menuItemExport -> {
                Log.d(LOG_TAG, "R.id.menuItemExport")
                viewModel.getAllWordsCor().observe(this, exportObserver)
                true
            }
            R.id.menuItemFind -> {
                Log.d(LOG_TAG, "R.id.menuItemFind")
                if (llSearch.visibility != View.VISIBLE) {
                    // show edit with close button
                    llSearch.visibility = View.VISIBLE
                    // pop-up IAST keyboard for now
                    // as typing, filter
                    true

                } else {
                    false
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CODE_REQUEST_ADD_WORD, CODE_REQUEST_EDIT_WORD -> {
                    val word = data?.getParcelableExtra<Word>(EXTRA_WORD_RESULT)

                    if (requestCode == CODE_REQUEST_ADD_WORD) {
                        if (llSearch.visibility == View.VISIBLE) { // the insertion was made from search
                            val filterEn = editSearchEnDic.text.toString()
                            val filterIast = editSearchIastDic.text.toString()
                            viewModel.filter2(filterEn, filterIast)
                                .observe(this@DictionaryActivity, { filteredWords ->
                                    wordsAdapter.words = filteredWords
                                })
                        } else {
                            viewModel.getAllWordsCor().observe(this@DictionaryActivity, {
                                wordsAdapter.words = it // show all words for now
                            })
                        }
                    } else if (requestCode == CODE_REQUEST_EDIT_WORD) {
                        if (llSearch.visibility == View.VISIBLE) { // the update was made from search
                            val filterEn = editSearchEnDic.text.toString()
                            val filterIast = editSearchIastDic.text.toString()
                            viewModel.filter2(filterEn, filterIast)
                                .observe(this@DictionaryActivity, { filteredWords ->
                                    wordsAdapter.words = filteredWords
                                })
                        } else {
                            viewModel.getAllWordsCor().observe(this@DictionaryActivity, {
                                wordsAdapter.words = it // show all words for now
                            })
                        }
                    } else {
                        Log.e(LOG_TAG, "success unknown code")
                    }
                }
                PICKFILE_RESULT_CODE -> {
                    val uri = data?.data
                    Log.d(LOG_TAG, uri?.path.toString())
                    if (uri != null) {
                        viewModel.readFromFiles(uri)
                            .observe(this@DictionaryActivity, importObserver)
                    } else {
                        Log.d(LOG_TAG, "path is null")
                    }
                }
                else -> {
                    Log.e(LOG_TAG, "Unknown code on DictionaryActivity.onActivityResult()")
                }
            }
        }
    }

    private fun initUI() {

        // TODO injection
        viewModel = ViewModelProvider(this@DictionaryActivity).get(WordsViewModel::class.java)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = resources.getString(R.string.dictionary)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // search bar
        llSearch = findViewById(R.id.llSearchBar)
        editSearchEnDic = findViewById(R.id.editSearch)
        editSearchIastDic = findViewById(R.id.editSearchIast)
        ibSearchClose = findViewById(R.id.btnCloseSearchBar)

        editSearchEnDic.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(LOG_TAG, "[en] afterTextChanged: ${s.toString()}")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(LOG_TAG, "[en] beforeTextChanged: ${s.toString()}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(
                    LOG_TAG,
                    " [en] onTextChanged: ${s.toString()} start=$start before=$before count=$count"
                )
                val filterEn = s.toString()
                val filterIast = editSearchIastDic.text.toString()
                viewModel.filter2(filterEn, filterIast).observe(this@DictionaryActivity, {
                    wordsAdapter.words = it
                })
            }
        })

        editSearchEnDic.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val filterEn = (v as EditText).text.toString()
                val filterIast = editSearchIast.text.toString()
                viewModel.filter2(filterEn, filterIast).observe(this@DictionaryActivity, {
                    wordsAdapter.words = it
                })
            }
        }

        editSearchIastDic.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(LOG_TAG, "[iast] afterTextChanged: ${s.toString()}")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(LOG_TAG, "[iast] beforeTextChanged: ${s.toString()}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filterEn = editSearchEnDic.text.toString()
                val filterIast = s.toString()
                viewModel.filter2(filterEn, filterIast).observe(this@DictionaryActivity, {
                    wordsAdapter.words = it
                })
            }
        })

        editSearchIastDic.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val filterEn = editSearchEnDic.text.toString()
                val filterIast = (v as EditText).text.toString()
                viewModel.filter2(filterEn, filterIast).observe(this@DictionaryActivity, {
                    wordsAdapter.words = it
                })
            }
        }

        ibSearchClose.setOnClickListener {
            clearSearch()
            // close the keyboard
            hideSoftKeyboard(this@DictionaryActivity)
        }

        // get data from intent
        val searchIast = intent.getStringExtra(EXTRA_WORD_IAST)
        val searchEn = intent.getStringExtra(EXTRA_WORD_EN)
        if (!searchIast.isNullOrEmpty() || !searchEn.isNullOrEmpty()) {
            if (llSearch.visibility != View.VISIBLE) {
                // show edit with close button
                llSearch.visibility = View.VISIBLE
                if (!searchIast.isNullOrEmpty()) {
                    editSearchIastDic.setText(searchIast)
                }
                if (!searchEn.isNullOrEmpty()) {
                    editSearchEnDic.setText(searchEn)
                }
                if (searchIast != null) {
                    viewModel.filter(searchIast, true).observe(this@DictionaryActivity, { words1 ->
                        if (searchEn != null) {
                            viewModel.filter2(searchEn, searchIast)
                                .observe(this@DictionaryActivity, { words2 ->
                                    wordsAdapter.words = words2
                                })
                        } else {
                            wordsAdapter.words = words1
                        }
                    })
                }
            }
        }

        // words recycleview
        recyclerView = findViewById(R.id.rv_words)
        wordsAdapter = WordsAdapter(
            this, itemDefinitionClickListener,
            itemEditClickListener,
            longClickListener
        ) {
            llRemoveCancel.visibility = if (it) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = wordsAdapter
        viewModel.getAllWordsCor().observe(this, Observer<List<Word>> {
            wordsAdapter.words = it
        })

        // remove words
        llRemoveCancel = findViewById(R.id.llRemoveCancel)
        btnRemove.setOnClickListener(this::removeSelected)
        btnClearSelections.setOnClickListener(this::unselectAll)

        // add fab
        fab = findViewById(R.id.fabDictionary)
        fab.setOnClickListener {
            val intent = Intent(this@DictionaryActivity, AddModifyWordActivity::class.java)
            val word = Word(
                word = "",
                wordIAST = editSearchIast.text.toString(),
                meaningEn = editSearchEnDic.text.toString(),
                meaningRo = ""
            )
            intent.putExtra(EXTRA_WORD, word)
            intent.putExtra(EXTRA_REQUEST_CODE, CODE_REQUEST_ADD_WORD)
            startActivityForResult(intent, CODE_REQUEST_ADD_WORD)
        }
    }

    private fun clearSearch() {
        editSearchEnDic.text.clear()
        editSearchIastDic.text.clear()
        llSearch.visibility = View.GONE
    }

    private fun unselectAll(v: View) {
        (recyclerView.adapter as WordsAdapter).unselectRemoveSelected()  // todo fix
        llRemoveCancel.visibility = View.GONE
    }

    @SuppressLint("CheckResult", "RestrictedApi")
    private fun removeSelected(v: View) {
        val adapter = recyclerView.adapter as WordsAdapter
        viewModel.deleteWords(adapter.getWordsToRemove())
            .observe(this@DictionaryActivity, {
                adapter.unselectRemoveSelected()
                if (llSearch.visibility != View.VISIBLE) { // show all words
                    wordsAdapter.words = it
                } else { // filter
                    viewModel.filter2(
                        editSearchEnDic.text.toString(),
                        editSearchIastDic.text.toString()
                    ).observe(this@DictionaryActivity, { fw ->
                            wordsAdapter.words = fw
                        })
                }
            })
    }

    @SuppressLint("RestrictedApi")
    private fun cancelRemoveSelected() {
        updateRevViewItems(WordsAdapter.TYPE_NON_REMOVABLE)
        llRemoveCancel.visibility = View.GONE
        fab.visibility = View.VISIBLE
    }

    companion object {
        private val LOG_TAG = DictionaryActivity::class.java.simpleName
    }

    private val exportObserver = Observer<List<Word>> { words ->
        val filename = FILENAME_WORDS // todo make a constant for now
        if (words != null) {
            viewModel.writeToFiles(Words(words), filename).observe(this@DictionaryActivity, {
                this.export(filename = filename)
            })
        }
    }

    private val importObserver = Observer<List<Word>> {
        wordsAdapter.words = it
    }

    private val itemDefinitionClickListener: View.OnClickListener = View.OnClickListener {
        val word = it.tag as Word
        WordDialog(this, word).showDialog()
    }

    private val itemEditClickListener = View.OnClickListener {
        val intentEdit = Intent(this@DictionaryActivity, AddModifyWordActivity::class.java)
        intentEdit.putExtra(EXTRA_WORD, it.tag as Word)
        intentEdit.putExtra(EXTRA_REQUEST_CODE, CODE_REQUEST_EDIT_WORD)
        this@DictionaryActivity.startActivityForResult(intentEdit, CODE_REQUEST_EDIT_WORD)
    }

    @SuppressLint("RestrictedApi")
    private val longClickListener: View.OnLongClickListener = View.OnLongClickListener {
        updateRevViewItems(WordsAdapter.TYPE_REMOVABLE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab.visibility = View.GONE
        true
    }

    private fun updateRevViewItems(type: Int) {
        val adapter: WordsAdapter = recyclerView.adapter as WordsAdapter
        adapter.type = type
        adapter.notifyDataSetChanged()
    }

    override fun onSupportNavigateUp(): Boolean {
        cancelRemoveSelected()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return true
    }

    override fun onBackPressed() {
        if (llRemoveCancel.visibility == View.VISIBLE) {
            cancelRemoveSelected()
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            super.onBackPressed()
        }
    }
}