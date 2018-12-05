package com.android.lvicto.sanskriter.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapters.WordsAdapter
import com.android.lvicto.sanskriter.adapters.WordsAdapter.Companion.TYPE_NON_REMOVABLE
import com.android.lvicto.sanskriter.adapters.WordsAdapter.Companion.TYPE_REMOVABLE
import com.android.lvicto.sanskriter.data.Words
import com.android.lvicto.sanskriter.db.entity.Word
import com.android.lvicto.sanskriter.utils.Constants
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_WORD
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_WORD_ID
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.REQUEST_CODE_ADD_WORD
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.REQUEST_CODE_EDIT_WORD
import com.android.lvicto.sanskriter.utils.KeyboardHelper.hideSoftKeyboard
import com.android.lvicto.sanskriter.viewmodels.WordsViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class DictionaryActivity : AppCompatActivity() {

    private lateinit var viewModel: WordsViewModel
    private lateinit var wordsAdapter: WordsAdapter

    private lateinit var llRemoveCancel: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var llImport: LinearLayout
    private lateinit var llSearch: LinearLayout
    private lateinit var editSearchDic: EditText
    private lateinit var ibSearchClose: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        // todo remove (for testing only)
//        val db = WordsDatabase.getInstance(this)!!
//        db.popupateDbForTesting()

        // todo 0-state screen

        initUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dictionary, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.menuItemImport -> {
                Log.d(LOG_TAG, "R.id.menuItemImport")
                llImport.visibility = View.VISIBLE
                viewModel.loadFromPrivateFile().observe(this@DictionaryActivity, importObserver)
                true
            }
            R.id.menuItemExport -> {
                Log.d(LOG_TAG, "R.id.menuItemExport")
                viewModel.allWords.observe(this, exportObserver)
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
                REQUEST_CODE_ADD_WORD, REQUEST_CODE_EDIT_WORD -> {
                    val wordRo = data!!.getStringExtra(Constants.Keyboard.EXTRA_WORD_RO)
                    val wordEn = data.getStringExtra(Constants.Keyboard.EXTRA_WORD_WORD_EN)
                    val wordSa = data.getStringExtra(Constants.Keyboard.EXTRA_WORD_SA)
                    val wordIAST = data.getStringExtra(Constants.Keyboard.EXTRA_WORD_IAST)
                    val word = Word(word = wordSa, wordIAST = wordIAST, meaningEn = wordEn, meaningRo = wordRo)
                    if (data.hasExtra(EXTRA_WORD_ID)) {
                        word.id = data.getLongExtra(EXTRA_WORD_ID, -1L)
                    }
                    viewModel.insert(word)
                }
                else -> {
                    Log.e(LOG_TAG, "Unknown code on DictionaryActivity.onActivityResult()")
                }
            }
        }
    }

    private fun initUI() {

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = resources.getString(R.string.dictionary)
        setSupportActionBar(toolbar)

        // search bar
        llSearch = findViewById(R.id.llSearchBar)
        editSearchDic = findViewById(R.id.editSearchDic)
        ibSearchClose = findViewById(R.id.btnCloseSearchBar)

        editSearchDic.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(LOG_TAG, "afterTextChanged: ${s.toString()}")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(LOG_TAG, "beforeTextChanged: ${s.toString()}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG_TAG, "onTextChanged: ${s.toString()} start=$start before=$before count=$count")
                viewModel.filterWords(s.toString()).observe(this@DictionaryActivity, Observer<List<Word>> {
                    wordsAdapter.words = it
                })
            }
        })

        ibSearchClose.setOnClickListener {
            // todo clear search
            editSearchDic.text.clear()
            llSearch.visibility = View.GONE
            // close the keyboard
            hideSoftKeyboard(this@DictionaryActivity)
        }

        // import/export
        viewModel = ViewModelProviders.of(this).get(WordsViewModel::class.java)
        llImport = findViewById(R.id.llJsonImport)
        val edit = findViewById<EditText>(R.id.editJson)
        findViewById<Button>(R.id.btnLoadJson).setOnClickListener {
            viewModel.loadFromString(edit.text.toString()).observe(this@DictionaryActivity, loadFromJsonObserver)
            llImport.visibility = View.GONE
        }

        // words recycleview
        recyclerView = findViewById(R.id.rv_words)
        wordsAdapter = WordsAdapter(this, itemDefinitionClickListener, itemEditClickListener, longClickListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = wordsAdapter
        viewModel.allWords.observe(this, Observer<List<Word>> {
            wordsAdapter.words = it
        })

        // remove words
        llRemoveCancel = findViewById(R.id.llRemoveCancel)
        val btnRemove = findViewById<Button>(R.id.btnRemove)
        btnRemove.setOnClickListener(this::removeSelected)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            (recyclerView.adapter as WordsAdapter).unselectRemoveSelected()
            cancelRemoveSelected()
        }

        // add fab
        val fab = findViewById<FloatingActionButton>(R.id.fabDictionary)
        fab.setOnClickListener {
            val intent = Intent(this@DictionaryActivity, AddModifyWordActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_WORD)
        }
    }

    @SuppressLint("CheckResult")
    private fun removeSelected(v: View) {
        val adapter = recyclerView.adapter as WordsAdapter
        viewModel.deleteWords(adapter.getWordsToRemove()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::refreshWords)
    }

    private fun refreshWords(dummy: Int) {
        viewModel.refreshAllWords(0) // todo refactor
        viewModel.allWords.observe(this, Observer<List<Word>> {
            val adapter = recyclerView.adapter as WordsAdapter
            adapter.type = WordsAdapter.TYPE_NON_REMOVABLE
            adapter.words = it
            llRemoveCancel.visibility = View.GONE
        })
    }

    private fun cancelRemoveSelected() {
        updateRevViewItems(WordsAdapter.TYPE_NON_REMOVABLE)
        llRemoveCancel.visibility = View.GONE
    }

    companion object {
        private val LOG_TAG = DictionaryActivity::class.java.simpleName
    }

    private val loadFromJsonObserver = Observer<String> { it ->
        Log.d(LOG_TAG, it)
        if (!it!!.isEmpty()) {
            val list = Gson().fromJson(it, Words::class.java)
            list.list.forEach {
                Log.d(LOG_TAG, it.toString())
                viewModel.insert(it)
            }
        }
    }

//    private val saveToFileObserver = Observer<List<Word>> { it ->
//        if (it != null) {
    // save on private file
//            viewModel.saveToPrivateFile(Words(it)).observe(this@DictionaryActivity, Observer<()->Unit> {
//                it?.invoke() // todo make it return json string
    // todo launch share intent
//            })
//        }
//    }

    private fun export(words: List<Word>) {
        val jsonString = Gson().toJson(Words(words))
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sanskrit dic as json (String)")
        intent.putExtra(Intent.EXTRA_TEXT, jsonString)
        startActivity(Intent.createChooser(intent, "Share using")) // todo make a string resource
    }

    private val exportObserver = Observer<List<Word>> {
        export(it!!)
    }

    private val importObserver = Observer<String> {
        // todo
    }

    private val itemDefinitionClickListener: View.OnClickListener = View.OnClickListener {
        Toast.makeText(this, "${it.tag}", Toast.LENGTH_SHORT).show()
    }

    private val itemEditClickListener = View.OnClickListener {
        val intentEdit = Intent(this@DictionaryActivity, AddModifyWordActivity::class.java)
        intentEdit.putExtra(EXTRA_WORD, it!!.tag as Word)
        this@DictionaryActivity.startActivityForResult(intentEdit, REQUEST_CODE_EDIT_WORD)
    }

    private val longClickListener: View.OnLongClickListener = View.OnLongClickListener {
        Toast.makeText(this, "Long tap", Toast.LENGTH_LONG).show()
        updateRevViewItems(WordsAdapter.TYPE_REMOVABLE)
        llRemoveCancel.visibility = View.VISIBLE
        true
    }

    private fun updateRevViewItems(type: Int) {
        val adapter: WordsAdapter = recyclerView.adapter as WordsAdapter
        adapter.type = type
        adapter.notifyDataSetChanged()

    }

    override fun onBackPressed() {
        when {
            llImport.visibility == View.VISIBLE -> llImport.visibility = View.GONE
            llRemoveCancel.visibility == View.VISIBLE -> cancelRemoveSelected()
            else -> super.onBackPressed()
        }
    }
}