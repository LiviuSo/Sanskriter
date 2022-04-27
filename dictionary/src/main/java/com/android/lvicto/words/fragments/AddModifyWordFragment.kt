package com.android.lvicto.words.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import com.android.lvicto.R
import com.android.lvicto.common.Constants
import com.android.lvicto.common.Constants.EXTRA_REQUEST_CODE
import com.android.lvicto.common.Constants.EXTRA_WORD
import com.android.lvicto.common.WordWrapper
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.navigateBack
import com.android.lvicto.common.base.BaseFragment
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.*
import com.android.lvicto.db.entity.Word
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.activities.AddModifyWordActivity
import com.android.lvicto.words.usecase.WordsInsertUseCase
import com.android.lvicto.words.usecase.WordsUpdateUseCase
import kotlinx.android.synthetic.main.fragment_add_word.view.*
import kotlinx.coroutines.*

class AddModifyWordFragment : BaseFragment() {

    private var word: Word? = null // todo make it a MediatorLiveData
    private var requestCode: Int = -1
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)


    @field:Service
    private lateinit var converters: Converters

    @field:Service
    private lateinit var wordsInsertWordsUseCase: WordsInsertUseCase

    @field:Service
    private lateinit var wordsUpdateUseCase: WordsUpdateUseCase

    @field:Service
    private lateinit var dialogManager: DialogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            injector.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val activity = requireActivity()

        val root = inflater.inflate(R.layout.fragment_add_word, container, false)

        converters = Converters() // todo inject

        requestCode = arguments?.getInt(EXTRA_REQUEST_CODE) ?: -1
        word = arguments?.getParcelable(EXTRA_WORD)

        // todo move to viewMvc
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
                        gType = converters.toGrammaticalType(
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
                    word?.gender = converters.toGrammaticalGender(
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

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
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
        val gType = converters.toGrammaticalType(root.spinnerType.selectedItem.toString())
        val paradigm = root.editParadigm.text.toString()
        val verbClass = VerbClass.toVerbClassFromName(root.spinnerVerbCase.selectedItem.toString())
        val gender = converters.toGrammaticalGender(root.spinnerWordGender.selectedItem.toString())

        val newWord = Word(
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
            Constants.CODE_REQUEST_ADD_WORD -> {
                coroutineScope.launch {
                    addWord(v, newWord, activity)
                }
            }
            Constants.CODE_REQUEST_EDIT_WORD -> {
                coroutineScope.launch {
                    newWord.id = word?.id ?: -1 // if null no modification will happen
                    modifyWord(v, newWord, activity)
                }
            }
            else -> {
                Log.d(LOG_ADD_MODIFY, "onClickAdd() : No word")
                dialogManager.showErrorDialog(R.string.dialog_error_message_words_added)
            }
        }
    }

    private suspend fun modifyWord(
        view: View,
        word: Word,
        activity: FragmentActivity
    ) {
//        val result = wordsUpdateUseCase.updateWord(word) // todo remove when migration completed
        val result = wordsUpdateUseCase.updateWordPlus(word.toWordWrapper())
        if (result is WordsUpdateUseCase.Result.Success) {
            dialogManager.showInfoDialog(R.string.dialog_info_message_words_updated) {
                it.dismiss()
                if (activity is AddModifyWordActivity) {
                    activity.finish()
                } else {
                    view.navigateBack()
                }
            }
        } else if (result is WordsUpdateUseCase.Result.Failure) {
            Log.e(LOG_ADD_MODIFY, "Unable to update word: ${result.message}")
            dialogManager.showErrorDialog(R.string.dialog_error_message_words_update)
        }
    }

    private suspend fun addWord(
        view: View,
        word: Word,
        activity: FragmentActivity
    ) {
        val resultPlus = wordsInsertWordsUseCase.insertWordPlus(WordWrapper(
            gType = word.gType,
            wordSa = word.word,
            wordIAST = word.wordIAST,
            meaningEn = word.meaningEn,
            meaningRo = word.meaningRo,
            paradigm = word.paradigm,
            gender = word.gender,
            number = GrammaticalNumber.NONE,
            person = GrammaticalPerson.NONE,
            grammaticalCase = GrammaticalCase.NONE,
            verbClass = word.verbClass))

        val result = wordsInsertWordsUseCase.insertWord(word) // todo remove when migration complete
        if (result is WordsInsertUseCase.Result.Success) {
            dialogManager.showInfoDialog(R.string.dialog_info_message_word_added) {
                it.dismiss()
                if (activity is AddModifyWordActivity) {
                    activity.finish()
                } else {
                    view.navigateBack()
                }
            }
        } else if (result is WordsInsertUseCase.Result.Failure) {
            result.message?.let {
                Log.e(LOG_ADD_MODIFY, "Unable to update word: ${result.message}")
                dialogManager.showErrorDialog(R.string.dialog_error_message_words_added)
            }
        }
    }

    companion object {
        val LOG_ADD_MODIFY = AddModifyWordActivity::class.simpleName
    }

}