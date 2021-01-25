package com.android.lvicto.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.util.Constants.Dictionary.EXTRA_WORD
import com.android.lvicto.util.Constants.Dictionary.EXTRA_WORD_IAST
import com.android.lvicto.util.Constants.Dictionary.EXTRA_WORD_ID
import com.android.lvicto.util.Constants.Dictionary.EXTRA_WORD_RO
import com.android.lvicto.util.Constants.Dictionary.EXTRA_WORD_SA
import com.android.lvicto.util.Constants.Dictionary.EXTRA_WORD_EN
import com.android.lvicto.R
import com.android.lvicto.db.entity.Word
import com.android.lvicto.util.Constants.Dictionary.EXTRA_REQUEST_CODE
import com.android.lvicto.util.Constants.Dictionary.CODE_REQUEST_ADD_WORD
import com.android.lvicto.util.Constants.Dictionary.CODE_REQUEST_EDIT_WORD
import com.android.lvicto.viewmodel.WordsViewModel

class AddModifyWordActivity : AppCompatActivity() {

    private lateinit var editWord: EditText
    private lateinit var editWordIAST: EditText
    private lateinit var editWordRo: EditText
    private lateinit var editWordEn: EditText
    private lateinit var buttonSave: Button

    private var id: Long? = null
    private var requestCode: Int = -1

    private lateinit var viewModel: WordsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)

        viewModel = WordsViewModel(application)

        editWord = findViewById(R.id.editSa)
        editWordIAST = findViewById(R.id.editIAST)
        editWordRo = findViewById(R.id.editRo)
        editWordEn = findViewById(R.id.editEn)
        buttonSave = findViewById(R.id.btnSaveWord)

        if(intent.hasExtra(EXTRA_REQUEST_CODE)) {
            requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1)
        }
        if (intent.hasExtra(EXTRA_WORD)) {
            val word: Word? = intent.getParcelableExtra("EXTRA_WORD")
            id = word?.id
            editWord.setText(word?.word)
            editWordIAST.setText(word?.wordIAST)
            editWordRo.setText(word?.meaningRo)
            editWordEn.setText(word?.meaningEn)
        }
        buttonSave.setOnClickListener(this::onClickAdd)
    }

    private fun onClickAdd(v: View) {
        val wordSa = editWord.text.toString()
        val wordIAST = editWordIAST.text.toString()
        val wordEn = editWordEn.text.toString()
        val wordRo = editWordRo.text.toString()

        val word = Word(word = wordSa, wordIAST = wordIAST, meaningEn = wordEn, meaningRo = wordRo)
        when (requestCode) {
            CODE_REQUEST_ADD_WORD -> {
                viewModel.insert(word).observe(this, {
                    Toast.makeText(this@AddModifyWordActivity, "Added word.", Toast.LENGTH_SHORT).show()
                })
            }
            CODE_REQUEST_EDIT_WORD -> {
                viewModel.update(id ?: 0, word.word, word.wordIAST, word.meaningEn, word.meaningRo).observe(this@AddModifyWordActivity, {
                    Toast.makeText(this@AddModifyWordActivity, "Modified word.", Toast.LENGTH_SHORT).show()
                })
            }
            else -> {
                Log.d(LOG_ADD_MODIFY, "onClickAdd() : No word")
            }
        }

        // reply tot the calling activity
        val replyIntent = Intent()
        if (TextUtils.isEmpty(wordSa)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
            // don't do anything
        } else {
            if(id != null) {
                replyIntent.putExtra(EXTRA_WORD_ID, id!!)
            }
            replyIntent.putExtra(EXTRA_WORD_SA, wordSa)
            replyIntent.putExtra(EXTRA_WORD_IAST, wordIAST)
            replyIntent.putExtra(EXTRA_WORD_EN, wordEn)
            replyIntent.putExtra(EXTRA_WORD_RO, wordRo)
            setResult(Activity.RESULT_OK, replyIntent)
        }
        finish()
    }

    companion object {
        val LOG_ADD_MODIFY = AddModifyWordActivity::class.simpleName
    }
}