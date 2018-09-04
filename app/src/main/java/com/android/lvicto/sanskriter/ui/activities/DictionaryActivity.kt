package com.android.lvicto.sanskriter.ui.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapters.WordsAdapter
import com.android.lvicto.sanskriter.data.Words
import com.android.lvicto.sanskriter.db.WordsDatabase
import com.android.lvicto.sanskriter.db.entity.Word
import com.android.lvicto.sanskriter.utils.Constants
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_WORD
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.EXTRA_WORD_ID
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.REQUEST_CODE_ADD_WORD
import com.android.lvicto.sanskriter.utils.Constants.Keyboard.REQUEST_CODE_EDIT_WORD
import com.android.lvicto.sanskriter.viewmodels.WordsViewModel
import com.google.gson.Gson

class DictionaryActivity : AppCompatActivity() {

    private lateinit var viewModel: WordsViewModel
    private lateinit var llRemoveCancel: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var llImport: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        // for testing
//        val db = WordsDatabase.getInstance(this)!!
//        db.popupateDbForTesting()
        // todo 0-state screen

        initUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD_WORD, REQUEST_CODE_EDIT_WORD -> {
                    val wordRo = data!!.getStringExtra(Constants.Keyboard.EXTRA_WORD_RO)
                    val wordEn = data.getStringExtra(Constants.Keyboard.EXTRA_WORD_WORD_EN)
                    val wordSa = data.getStringExtra(Constants.Keyboard.EXTRA_WORD_SA)
                    val word = Word(word = wordSa, meaningEn = wordEn, meaningRo = wordRo)
                    if(data.hasExtra(EXTRA_WORD_ID)) {
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
        viewModel = ViewModelProviders.of(this).get(WordsViewModel::class.java)

        val btnLoad = findViewById<Button>(R.id.btnLoad)
        btnLoad.setOnClickListener { _ ->
            viewModel.loadFromPrivateFile().observe(AllWordsActivity@this, loadFromJsonObserver)
        }

        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener { _ ->
//            viewModel.allWords.observe(this, saveToFileObserver)
            // todo add Rx subcribe()
        }

        llImport = findViewById(R.id.llJsonImport)
        findViewById<Button>(R.id.btnImport).setOnClickListener { _ ->
            llImport.visibility = View.VISIBLE
            viewModel.loadFromPrivateFile().observe(AllWordsActivity@this, importObserver)
        }
        findViewById<Button>(R.id.btnExport).setOnClickListener { _ ->
            viewModel.allWords.observe(this, exportObserver)
//            viewModel.allWords.subscribe(this::export)
        }
        val edit = findViewById<EditText>(R.id.editJson)
        findViewById<Button>(R.id.btnLoadJson).setOnClickListener {
            viewModel.loadFromString(edit.text.toString()).observe(AllWordsActivity@this, loadFromJsonObserver)
            llImport.visibility = View.GONE
        }

        recyclerView = findViewById(R.id.rv_words)
        val wordsAdapter = WordsAdapter(this, itemClickListener, longClickListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = wordsAdapter
        viewModel.allWords.observe(this, Observer<List<Word>> {
            wordsAdapter.words = it
        })
//        viewModel.allWords.subscribe(this::setAdapterData)

        llRemoveCancel = findViewById(R.id.llRemoveCancel)
        val btnRemove = findViewById<Button>(R.id.btnRemove)
        btnRemove.setOnClickListener(this::removeSelected)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            (recyclerView.adapter as WordsAdapter).unselectRemoveSelected()
            cancelRemoveSelected()
        }

        val fab = findViewById<FloatingActionButton>(R.id.fabDictionary)
        fab.setOnClickListener {
            val intent = Intent(MainActivity@this, AddModifyWordActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_WORD)
        }
    }

    private fun setAdapterData(words: List<Word>) {
        (recyclerView.adapter as WordsAdapter).words = words
    }

    private fun removeSelected(v: View) {
        val adapter = recyclerView.adapter as WordsAdapter
        viewModel.deleteWords(adapter.getWordsToRemove()).subscribe(this::log)
    }

    private fun log(c: Int) {
        Log.d(LOG_TAG, "Deleted $c rows")
    }

//    private fun refreshWords(dummy: Int) {
//        viewModel.allWords.observe(this, Observer<List<Word>> {
//            val adapter = recyclerView.adapter as WordsAdapter
//            adapter.words = it
//            updateRevViewItems(WordsAdapter.TYPE_NON_REMOVABLE)
//            llRemoveCancel.visibility = View.GONE
//        })
//    }

    private fun cancelRemoveSelected() {
        updateRevViewItems(WordsAdapter.TYPE_NON_REMOVABLE)
        llRemoveCancel.visibility = View.GONE
    }

    companion object {
        private val LOG_TAG = DictionaryActivity::class.java.simpleName
    }

    private val loadFromJsonObserver = Observer<String> { it ->
        Log.d(LOG_TAG, it)
        if(!it!!.isEmpty()) {
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

    private val itemClickListener = View.OnClickListener {
        val intentEdit = Intent(AllWordsActivity@this, AddModifyWordActivity::class.java)
        intentEdit.putExtra(EXTRA_WORD, it!!.tag as Word)
        AllWordsActivity@this.startActivityForResult(intentEdit, REQUEST_CODE_EDIT_WORD)
    }

    private val longClickListener: View.OnLongClickListener = View.OnLongClickListener {
        Toast.makeText(this, "Long tap", Toast.LENGTH_SHORT).show()
        updateRevViewItems(WordsAdapter.TYPE_REMOVABLE)
        llRemoveCancel.visibility = View.VISIBLE
        true
    }

    private fun updateRevViewItems(type: Int) {
        val adapter: WordsAdapter = recyclerView.adapter as WordsAdapter
//        val count = adapter.itemCount
        adapter.type = type
//        recyclerView.adapter.notifyItemRangeChanged(0, count)
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
