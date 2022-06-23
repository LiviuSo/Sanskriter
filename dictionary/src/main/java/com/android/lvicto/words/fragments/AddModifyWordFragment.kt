package com.android.lvicto.words.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import com.android.lvicto.R
import com.android.lvicto.common.Constants.CODE_REQUEST_ADD_WORD
import com.android.lvicto.common.Constants.CODE_REQUEST_EDIT_WORD
import com.android.lvicto.common.Constants.EXTRA_MODE_CODE
import com.android.lvicto.common.Constants.EXTRA_REQUEST_CODE
import com.android.lvicto.common.Constants.EXTRA_WORD
import com.android.lvicto.common.Constants.MODE_EDIT_WORD
import com.android.lvicto.common.Constants.MODE_VIEW_WORD
import com.android.lvicto.common.Word
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.navigateBack
import com.android.lvicto.common.base.BaseFragment
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.*
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.usecase.WordsInsertUseCase
import com.android.lvicto.words.usecase.WordsUpdateUseCase
import kotlinx.android.synthetic.main.fragment_add_word.view.*
import kotlinx.coroutines.*

class AddModifyWordFragment : BaseFragment() {

    private var mode: Int? = null
    private var word: Word? = null // todo make it a MediatorLiveData
    private var oldWord: Word? = null // todo make it a MediatorLiveData
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val activity = requireActivity()
        val root = inflater.inflate(R.layout.fragment_add_word, container, false)

        converters = Converters() // todo inject

        arguments?.apply {
            mode = getInt(EXTRA_MODE_CODE)
            requestCode = getInt(EXTRA_REQUEST_CODE)
            word = getParcelable(EXTRA_WORD)
            oldWord = Word(id = word?.id ?: -1,
                gType = word?.gType ?: GrammaticalType.OTHER,
                wordSa = word?.wordSa ?: "",
                wordIAST = word?.wordIAST ?: "",
                meaningEn = word?.meaningEn ?: "",
                meaningRo = word?.meaningRo ?: "",
                paradigm = word?.paradigm ?: "",
                gender = word?.gender ?: GrammaticalGender.NONE,
                number = word?.number ?: GrammaticalNumber.NONE,
                person = word?.person ?: GrammaticalPerson.NONE,
                grammaticalCase = word?.grammaticalCase ?: GrammaticalCase.NONE,
                verbClass = word?.verbClass ?: VerbClass.NONE
            )
        }

        // todo move to viewMvc
        return root.apply {
            showModeIcon(this)

            ibEditLock.setOnClickListener {
                mode = if(mode == MODE_EDIT_WORD)
                    MODE_VIEW_WORD
                else
                    MODE_EDIT_WORD

                showModeIcon(this)

                enableOrDisableByMode(editSa)
                enableOrDisableByMode(editIAST)
                enableOrDisableByMode(editRo)
                enableOrDisableByMode(editEn)
                enableOrDisableByMode(editParadigm)
                enableOrDisableByMode(spinnerType)
                enableOrDisableByMode(spinnerWordGender)
                enableOrDisableByMode(spinnerVerbCase)
                showOrHideByMode(btnSaveWord)
            }

            editSa?.apply {
                setText(word?.wordSa)
                enableOrDisableByMode(this)
            }
            editIAST?.apply {
                setText(word?.wordIAST)
                enableOrDisableByMode(this)
            }
            editRo?.apply {
                setText(word?.meaningRo)
                enableOrDisableByMode(this)
            }
            editEn?.apply {
                setText(word?.meaningEn)
                enableOrDisableByMode(this)
            }
            editParadigm?.apply {
                setText(word?.paradigm)
                enableOrDisableByMode(this)
            }

            spinnerType.apply {
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
                setSelection(GrammaticalType.getPosition(word?.gType))
                enableOrDisableByMode(this)
            }

            spinnerWordGender.apply {
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
                setSelection(GrammaticalGender.getPosition(word?.gender))
                enableOrDisableByMode(this)
            }

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
                        word?.verbClass = VerbClass.toVerbClassFromName(
                            parent?.getItemAtPosition(position).toString()
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        word?.verbClass = VerbClass.NONE
                    }
                }
                setSelection(VerbClass.getPosition(word?.verbClass))
                enableOrDisableByMode(this)
            }

            showHideField(root, word)

            btnSaveWord?.apply {
                setOnClickListener(this@AddModifyWordFragment::onClickAdd)
                showOrHideByMode(this)
            }
        }
    }

    private fun enableOrDisableByMode(view: View) {
        view.isEnabled = mode == MODE_EDIT_WORD
    }

    private fun showOrHideByMode(view: View) {
        view.isGone = mode == MODE_VIEW_WORD
    }

    private fun showModeIcon(root: View) {
        root.ibEditLock.background = if(mode == MODE_EDIT_WORD)
            resources.getDrawable(R.drawable.ic_edit_24, null)
        else
            resources.getDrawable(R.drawable.ic_lock_24, null)
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
    }

    private fun showHideField(root: View, word: Word?) {
        with(root) {
            when (word?.gType) {
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
    }

    private fun onClickAdd(v: View) {
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
            gType = gType,
            wordSa = wordSa,
            wordIAST = wordIAST,
            meaningEn = wordEn,
            meaningRo = wordRo,
            paradigm = paradigm,
            gender = gender,
            verbClass = verbClass
        )
        when (requestCode) {
            CODE_REQUEST_ADD_WORD -> {
                coroutineScope.launch {
                    addWord(v, newWord)
                }
            }
            CODE_REQUEST_EDIT_WORD -> {
                coroutineScope.launch {
                    oldWord?.let {
                        newWord.id = it.id // if null no modification will happen
                        modifyWord(v, it, newWord)
                    }
                }
            }
            else -> {
                Log.d(LOG_ADD_MODIFY, "onClickAdd() : No word")
                dialogManager.showErrorDialog(R.string.dialog_error_message_words_added)
            }
        }
    }

    private suspend fun modifyWord(view: View, oldWord: Word, newWord: Word) {
        val result = wordsUpdateUseCase.updateWord(oldWord, newWord)
        if (result is WordsUpdateUseCase.Result.Success) {
            dialogManager.showInfoDialog(R.string.dialog_info_message_words_updated) {
                it.dismiss()
                view.navigateBack()
            }
        } else if (result is WordsUpdateUseCase.Result.Failure) {
            Log.e(LOG_ADD_MODIFY, "Unable to update word: ${result.message}")
            dialogManager.showErrorDialog(R.string.dialog_error_message_words_update)
        }
    }

    private suspend fun addWord(view: View, word: Word) {
        val result = wordsInsertWordsUseCase.insertWord(Word(
            gType = word.gType,
            wordSa = word.wordSa,
            wordIAST = word.wordIAST,
            meaningEn = word.meaningEn,
            meaningRo = word.meaningRo,
            paradigm = word.paradigm,
            gender = word.gender,
            number = GrammaticalNumber.NONE,
            person = GrammaticalPerson.NONE,
            grammaticalCase = GrammaticalCase.NONE,
            verbClass = word.verbClass))

        if (result is WordsInsertUseCase.Result.Success) {
            dialogManager.showInfoDialog(R.string.dialog_info_message_word_added) {
                it.dismiss()
                view.navigateBack()
            }
        } else if (result is WordsInsertUseCase.Result.Failure) {
            result.message?.let {
                Log.e(LOG_ADD_MODIFY, "Unable to update word: ${result.message}")
                dialogManager.showErrorDialog(R.string.dialog_error_message_words_added)
            }
        }
    }

    companion object {
        const val LOG_ADD_MODIFY = "add_modify_word"

        @JvmStatic
        fun createBundle(word: Word, codeRequest: Int, mode: Int) = Bundle().apply {
            putParcelable(EXTRA_WORD, word)
            putInt(EXTRA_REQUEST_CODE, codeRequest)
            putInt(EXTRA_MODE_CODE, mode)
        }
    }

}