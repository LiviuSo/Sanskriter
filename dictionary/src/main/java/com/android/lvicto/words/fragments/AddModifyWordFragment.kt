package com.android.lvicto.words.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.lvicto.R
import com.android.lvicto.common.fragment.BaseFragment
import com.android.lvicto.common.util.Constants
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.db.data.VerbClass
import com.android.lvicto.db.entity.Word
import com.android.lvicto.words.WordsViewModel
import com.android.lvicto.words.activities.AddModifyWordActivity
import kotlinx.android.synthetic.main.fragment_add_word.view.*

class AddModifyWordFragment : BaseFragment() {

    private var word: Word? = null // todo make it a MediatorLiveData
    private var requestCode: Int = -1
    private var id: Long = -1

    private lateinit var viewModel: WordsViewModel
    private lateinit var converters: Converters

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity()

        val root = inflater.inflate(R.layout.fragment_add_word, container, false)

        viewModel = WordsViewModel(activity.application)

        converters = Converters() // todo Koin

        activity.intent.let {
            if (it.hasExtra(Constants.Dictionary.EXTRA_REQUEST_CODE)) {
                requestCode = it.getIntExtra(Constants.Dictionary.EXTRA_REQUEST_CODE, -1)
            }
            if (it.hasExtra(Constants.Dictionary.EXTRA_WORD)) {
                word = it.getParcelableExtra(Constants.Dictionary.EXTRA_WORD)
                id = word?.id ?: -1
                Log.d("david", "init with: $word")

            }
        }

        root.editSa.setText(word?.word)
        root.editIAST.setText(word?.wordIAST)
        root.editRo.setText(word?.meaningRo)
        root.editEn.setText(word?.meaningEn)
        root.editParadigm.setText(word?.paradigm)

        root.spinnerType.apply {
            adapter = ArrayAdapter.createFromResource(
                activity,
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
                    word?.apply {
                        gType = converters.toGramaticalType(
                            parent?.getItemAtPosition(position).toString()
                        )
                        showHideField(root, this)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    word?.apply {
                        gType = GrammaticalType.OTHER
                        showHideField(root, this)
                    }
                }
            }
        }
        root.spinnerType.setSelection(GrammaticalType.getPosition(word?.gType))

        root.spinnerWordGender.apply {
            adapter = ArrayAdapter.createFromResource(
                activity,
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
                    word?.gender = converters.toGramaticalGender(
                        parent?.getItemAtPosition(position).toString()
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    word?.gender = GrammaticalGender.NONE
                }

            }
        }
        root.spinnerWordGender.setSelection(GrammaticalGender.getPosition(word?.gender))

        root.spinnerVerbCase.apply {
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
                    word?.verbClass = VerbClass.toVerbClassFromName(
                        parent?.getItemAtPosition(position).toString()
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    word?.verbClass = VerbClass.NONE
                }
            }
        }
        root.spinnerVerbCase.setSelection(VerbClass.getPosition(word?.verbClass))

        showHideField(root, word)

        root.btnSaveWord.setOnClickListener(this::onClickAdd)

        return root
    }

    private fun showHideField(root: View, word: Word?) {
        when (word?.gType) {
            GrammaticalType.PROPER_NOUN -> {
                root.editParadigm.visibility = View.VISIBLE
                root.spinnerWordGender.visibility = View.GONE
                root.spinnerVerbCase.visibility = View.GONE
            }
            GrammaticalType.NOUN, GrammaticalType.ADJECTIVE -> {
                root.editParadigm.visibility = View.VISIBLE
                root.spinnerWordGender.visibility = View.VISIBLE
                root.spinnerVerbCase.visibility = View.GONE
            }
            GrammaticalType.VERB -> {
                root.editParadigm.visibility = View.GONE
                root.spinnerWordGender.visibility = View.GONE
                root.spinnerVerbCase.visibility = View.VISIBLE
            }
            else -> {
                root.editParadigm.visibility = View.GONE
                root.spinnerWordGender.visibility = View.GONE
                root.spinnerVerbCase.visibility = View.GONE
            }
        }
    }

    private fun onClickAdd(v: View) {
        val activity = requireActivity()
        val root = v.rootView
        val wordSa = root.editSa.text.toString()
        val wordIAST = root.editIAST.text.toString()
        val wordEn = root.editEn.text.toString()
        val wordRo = root.editRo.text.toString()
        val gType = converters.toGramaticalType(root.spinnerType.selectedItem.toString())
        val paradigm = root.editParadigm.text.toString()
        val verbClass = VerbClass.toVerbClassFromName(root.spinnerVerbCase.selectedItem.toString())
        val gender = converters.toGramaticalGender(root.spinnerWordGender.selectedItem.toString())

        val word = Word(
            id = id,
            word = wordSa,
            wordIAST = wordIAST,
            meaningEn = wordEn,
            meaningRo = wordRo,
            gType = gType,
            paradigm = paradigm,
            verbClass = verbClass,
            gender = gender
        )
        when (requestCode) {
            Constants.Dictionary.CODE_REQUEST_ADD_WORD -> {
                viewModel.insert(word).observe(this, {
                    Toast.makeText(activity, "Added word.", Toast.LENGTH_SHORT)
                        .show()
                    // reply to the calling activity
                    val replyIntent = Intent()
                    if (TextUtils.isEmpty(wordSa)) {
                        activity.setResult(Activity.RESULT_CANCELED, replyIntent)
                        // don't do anything
                    } else {
                        replyIntent.putExtra(Constants.Dictionary.EXTRA_WORD_RESULT, word)
                        activity.setResult(Activity.RESULT_OK, replyIntent)
                    }
                    activity.finish()
                })
            }
            Constants.Dictionary.CODE_REQUEST_EDIT_WORD -> {
                Log.d("david", "sending $word")
                viewModel.update(
                    word.id,
                    word.word,
                    word.wordIAST,
                    word.meaningEn,
                    word.meaningRo,
                    word.gType,
                    word.paradigm,
                    word.verbClass,
                    word.gender
                )
                    .observe(activity, {
                        Toast.makeText(
                            activity,
                            "Modified word : ${word.wordIAST}",
                            Toast.LENGTH_SHORT
                        ).show() // todo show dialog
                        // reply to the calling activity
                        val replyIntent = Intent()
                        replyIntent.putExtra(Constants.Dictionary.EXTRA_WORD_RESULT, word)
                        activity.setResult(Activity.RESULT_OK, replyIntent)
                        activity.finish()
                    })
            }
            else -> {
                Log.d(LOG_ADD_MODIFY, "onClickAdd() : No word")
                val replyIntent = Intent()
                activity.setResult(Activity.RESULT_CANCELED, replyIntent)
                activity.finish()
            }
        }
    }

    companion object {
        val LOG_ADD_MODIFY = AddModifyWordActivity::class.simpleName
    }


}