package com.android.lvicto.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.common.util.Constants.Dictionary.EXTRA_WORD
import com.android.lvicto.R
import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.db.data.VerbClass
import com.android.lvicto.db.Converters
import com.android.lvicto.db.entity.Word
import com.android.lvicto.common.util.Constants.Dictionary.EXTRA_REQUEST_CODE
import com.android.lvicto.common.util.Constants.Dictionary.CODE_REQUEST_ADD_WORD
import com.android.lvicto.common.util.Constants.Dictionary.CODE_REQUEST_EDIT_WORD
import com.android.lvicto.common.util.Constants.Dictionary.EXTRA_WORD_RESULT
import com.android.lvicto.viewmodel.WordsViewModel
import kotlinx.android.synthetic.main.activity_add_declension.*
import kotlinx.android.synthetic.main.activity_add_word.*

class AddModifyWordActivity : AppCompatActivity() {

    private var word: Word? = null // todo make it a MediatorLiveData
    private var requestCode: Int = -1
    private var id: Long = -1

    private lateinit var viewModel: WordsViewModel
    private lateinit var converters: Converters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)

        viewModel = WordsViewModel(application)

        converters = Converters() // todo Koin

        if (intent.hasExtra(EXTRA_REQUEST_CODE)) {
            requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1)
        }
        if (intent.hasExtra(EXTRA_WORD)) {
            word = intent.getParcelableExtra(EXTRA_WORD)
            id = word?.id ?: -1
            Log.d("david", "init with: $word")

        }

        editSa.setText(word?.word)
        editIAST.setText(word?.wordIAST)
        editRo.setText(word?.meaningRo)
        editEn.setText(word?.meaningEn)
        editParadigm.setText(word?.paradigm)

        spinnerType.apply {
            adapter = ArrayAdapter.createFromResource(
                this@AddModifyWordActivity,
                R.array.grammatical_types,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    word?.gType =
                        converters.toGramaticalType(parent?.getItemAtPosition(position).toString())
                    showHideField(word)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    word?.gType = GrammaticalType.OTHER
                    showHideField(word)
                }
            }
        }
        spinnerType.setSelection(GrammaticalType.getPosition(word?.gType))

        spinnerWordGender.apply {
            adapter = ArrayAdapter.createFromResource(this@AddModifyWordActivity,
            R.array.filter_sanskrit_gender_array,
                android.R.layout.simple_spinner_item
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    word?.gender = converters.toGramaticalGender(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    word?.gender = GrammaticalGender.NONE
                }

            }
        }
        spinnerWordGender.setSelection(GrammaticalGender.getPosition(word?.gender))

        spinnerVerbCase.apply {
            adapter = ArrayAdapter.createFromResource(
                context,
                R.array.verb_class,
                android.R.layout.simple_spinner_item
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    word?.verbClass = VerbClass.toVerbClassFromName(parent?.getItemAtPosition(position).toString())
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    word?.verbClass = VerbClass.NONE
                }
            }
        }
        spinnerVerbCase.setSelection(VerbClass.getPosition(word?.verbClass))

        showHideField(word)

        btnSaveWord.setOnClickListener(this::onClickAdd)
    }

    private fun showHideField(word: Word?) {
        when(word?.gType) {
            GrammaticalType.PROPER_NOUN -> {
                editParadigm.visibility = View.VISIBLE
                spinnerWordGender.visibility = View.GONE
                spinnerVerbCase.visibility = View.GONE
            }
            GrammaticalType.NOUN, GrammaticalType.ADJECTIVE -> {
                editParadigm.visibility = View.VISIBLE
                spinnerWordGender.visibility = View.VISIBLE
                spinnerVerbCase.visibility = View.GONE
            }
            GrammaticalType.VERB -> {
                editParadigm.visibility = View.GONE
                spinnerWordGender.visibility = View.GONE
                spinnerVerbCase.visibility = View.VISIBLE
            }
            else -> {
                editParadigm.visibility = View.GONE
                spinnerWordGender.visibility = View.GONE
                spinnerVerbCase.visibility = View.GONE
            }
        }
    }

    private fun onClickAdd(v: View) {
        val wordSa = editSa.text.toString()
        val wordIAST = editIAST.text.toString()
        val wordEn = editEn.text.toString()
        val wordRo = editRo.text.toString()
        val gType = converters.toGramaticalType(spinnerType.selectedItem.toString())
        val paradigm = editParadigm.text.toString()
        val verbClass = VerbClass.toVerbClassFromName(spinnerVerbCase.selectedItem.toString())
        val gender = converters.toGramaticalGender(spinnerWordGender.selectedItem.toString())

        val word = Word(id = id,
            word = wordSa,
            wordIAST = wordIAST,
            meaningEn = wordEn,
            meaningRo = wordRo,
            gType = gType,
            paradigm = paradigm,
            verbClass = verbClass,
            gender = gender)
        when (requestCode) {
            CODE_REQUEST_ADD_WORD -> {
                viewModel.insert(word).observe(this, {
                    Toast.makeText(this@AddModifyWordActivity, "Added word.", Toast.LENGTH_SHORT)
                        .show()
                })
            }
            CODE_REQUEST_EDIT_WORD -> {
                Log.d("david", "sending $word")
                viewModel.update(word.id,
                    word.word,
                    word.wordIAST,
                    word.meaningEn,
                    word.meaningRo,
                    word.gType,
                    word.paradigm,
                    word.verbClass,
                    word.gender)
                    .observe(this@AddModifyWordActivity, {
                        Toast.makeText(
                            this@AddModifyWordActivity,
                            "Modified word : ${word.wordIAST}",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
            }
            else -> {
                Log.d(LOG_ADD_MODIFY, "onClickAdd() : No word")
            }
        }

        // reply to the calling activity
        val replyIntent = Intent()
        if (TextUtils.isEmpty(wordSa) ) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
            // don't do anything
        } else {
            replyIntent.putExtra(EXTRA_WORD_RESULT, word)
            setResult(Activity.RESULT_OK, replyIntent)
        }
        finish()
    }

    companion object {
        val LOG_ADD_MODIFY = AddModifyWordActivity::class.simpleName
    }
}