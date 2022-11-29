package com.android.lvicto.words.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.R
import com.android.lvicto.common.Constants.CODE_REQUEST_ADD_WORD
import com.android.lvicto.common.Constants.CODE_REQUEST_EDIT_WORD
import com.android.lvicto.common.Constants.EXTRA_MODE_CODE
import com.android.lvicto.common.Constants.EXTRA_REQUEST_CODE
import com.android.lvicto.common.Constants.EXTRA_WORD
import com.android.lvicto.common.Constants.MODE_EDIT_WORD
import com.android.lvicto.common.Constants.MODE_VIEW_WORD
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseFragment
import com.android.lvicto.common.base.TextWatcherImpl
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.navigateBack
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.*
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.declension.adapter.DeclensionAdapter
import com.android.lvicto.declension.adapter.DeclensionEngine
import com.android.lvicto.declension.fragment.DeclensionFragment
import com.android.lvicto.declension.usecase.DeclensionFetchUseCase
import com.android.lvicto.declension.usecase.DeclensionFilterUseCase
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.usecase.WordsInsertUseCase
import com.android.lvicto.words.usecase.WordsUpdateUseCase
import com.android.lvicto.words.view.AddModifyWordViewMvc
import com.android.lvicto.words.view.AddModifyWordViewMvcImpl
import kotlinx.android.synthetic.main.fragment_add_word.view.*
import kotlinx.coroutines.*

class AddModifyWordFragment : BaseFragment() {

    private var mode: Int? = null
    private var word: Word? = null // todo make it a MediatorLiveData
    private var oldWord: Word? = null // todo make it a MediatorLiveData
    private var requestCode: Int = -1
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var mViewMvcImpl: AddModifyWordViewMvc
    private lateinit var root: View

    @field:Service
    private lateinit var converters: Converters

    @field:Service
    private lateinit var wordsInsertWordsUseCase: WordsInsertUseCase

    @field:Service
    private lateinit var wordsUpdateUseCase: WordsUpdateUseCase

    @field:Service
    private lateinit var dialogManager: DialogManager

    @field:Service
    private lateinit var declensionFetchUseCase: DeclensionFetchUseCase

    @field:Service
    private lateinit var declensionFilterUseCase: DeclensionFilterUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            injector.inject(this)
        }

        converters = Converters() // todo inject

        arguments?.apply {
            mode = getInt(EXTRA_MODE_CODE)
            requestCode = getInt(EXTRA_REQUEST_CODE)
            word = getParcelable(EXTRA_WORD)
            oldWord = Word(
                id = word?.id ?: -1,
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_add_word, container, false).apply {
            root = this
        }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }

    private fun refreshUI() {
        val activity = requireActivity()
        root.apply {
            showModeIcon(this)
            ibEditLock.setOnClickListener {
                toggleMode(this)
            }

            editSa?.apply {
                setText(oldWord?.wordSa)
                enableOrDisableByMode(this, mode == MODE_EDIT_WORD)
            }
            editIAST?.apply {
                setText(oldWord?.wordIAST)
                enableOrDisableByMode(this, mode == MODE_EDIT_WORD)
            }
            editRo?.apply {
                setText(oldWord?.meaningRo)
                enableOrDisableByMode(this, mode == MODE_EDIT_WORD)
            }
            editEn?.apply {
                setText(oldWord?.meaningEn)
                enableOrDisableByMode(this, mode == MODE_EDIT_WORD)
            }

            spinnerParadigm.apply {
                adapter = ArrayAdapter.createFromResource(activity, R.array.paradigms, android.R.layout.simple_spinner_item).also { adapter ->
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
                            paradigm = Paradigm.fromDescription(parent?.getItemAtPosition(position).toString())
                            showHideField(root, this)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        word?.apply {
                            paradigm = Paradigm.PARADIGM.paradigm
                            showHideField(root, this)
                        }
                    }
                }
                setSelection(Paradigm.getPosition(oldWord?.paradigm ?: Paradigm.PARADIGM.paradigm))
                enableOrDisableByMode(this, mode == MODE_EDIT_WORD)
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
                setSelection(GrammaticalType.getPosition(oldWord?.gType))
                enableOrDisableByMode(this, mode == MODE_EDIT_WORD)
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
                setSelection(GrammaticalGender.getPosition(oldWord?.gender))
                enableOrDisableByMode(this, mode == MODE_EDIT_WORD)
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
                setSelection(VerbClass.getPosition(oldWord?.verbClass))
                enableOrDisableByMode(this, mode == MODE_EDIT_WORD)
            }

            showHideField(root, oldWord)

            btnSaveWord?.apply {
                setOnClickListener(this@AddModifyWordFragment::onClickAdd)
                toggleVisibilityByMode(this, mode == MODE_VIEW_WORD)
            }

            setUpDeclensionFiltering(root)
        }
    }

    private fun toggleMode(root: View) {
        mode = if(mode == MODE_EDIT_WORD)
            MODE_VIEW_WORD
        else
            MODE_EDIT_WORD

        showModeIcon(root)

        enableOrDisableByMode(root.editSa, mode == MODE_EDIT_WORD)
        enableOrDisableByMode(root.editIAST, mode == MODE_EDIT_WORD)
        enableOrDisableByMode(root.editRo, mode == MODE_EDIT_WORD)
        enableOrDisableByMode(root.editEn, mode == MODE_EDIT_WORD)
        enableOrDisableByMode(root.spinnerParadigm, mode == MODE_EDIT_WORD)
        enableOrDisableByMode(root.spinnerType, mode == MODE_EDIT_WORD)
        enableOrDisableByMode(root.spinnerWordGender, mode == MODE_EDIT_WORD)
        enableOrDisableByMode(root.spinnerVerbCase, mode == MODE_EDIT_WORD)

        toggleVisibilityByMode(root.btnSaveWord, mode == MODE_VIEW_WORD)

        toggleDeclensionVisibility(root, oldWord)
        toggleConjugationVisibility(root, oldWord)
    }

    private fun isSubstantive(oldWord: Word?): Boolean =
        oldWord?.gType in arrayListOf(GrammaticalType.NOUN, GrammaticalType.ADJECTIVE, GrammaticalType.PROPER_NOUN)

    private fun shouldShowDeclension(word: Word?): Boolean =
        isSubstantive(word) && isParadigmImplemented(word) && mode == MODE_VIEW_WORD

    private fun onDetectDeclension(key: String) {
        coroutineScope.launch {
            oldWord?.let {
                mViewMvcImpl.setDeclensions(detectDeclension(key, it))
            }
        }
    }

    private suspend fun detectDeclension(keySuffix: String, word: Word): List<Declension> {
        val declensions = arrayListOf<Declension>()
        val filterKeyDeclension = Declension().apply {
            paradigm = word.paradigm
        }
        if(!isParadigmImplemented(word)) {
            Toast.makeText(requireContext(), "Paradigm not implemented yet.", Toast.LENGTH_SHORT).show()
        } else {
            if (keySuffix.isNotEmpty()) { // if empty return all declensions
                filterKeyDeclension.suffix = getLongestSuffix(keySuffix).ifEmpty {
                    keySuffix
                }
            }
            if (word.gender.abbr.isNotEmpty() && word.gender.abbr != GrammaticalGender.NONE.abbr) {
                filterKeyDeclension.gGender = word.gender
            }

            when (val result = declensionFilterUseCase.filter(filterKeyDeclension)) {
                is DeclensionFilterUseCase.Result.Success -> declensions.addAll(result.declensions)
                is DeclensionFilterUseCase.Result.Failure -> Log.d(DeclensionFragment.LOG_TAG, "Error filtering declensions")
                else -> { /* nothing */ }
            }
        }
        return declensions
    }

    private suspend fun getLongestSuffix(keySuffix: String): String {
        var index = keySuffix.length - 1
        val suffixes = hashSetOf<String>()
        while (index >= 0) {
            val result = declensionFetchUseCase.getSuffixes(keySuffix.substring(index, keySuffix.length))
            if (result is DeclensionFetchUseCase.Result.SuccessSuffixes)
                suffixes.addAll(result.declensions.distinct())
            index--
        }
        return if (suffixes.isEmpty()) {
            ""
        } else {
            suffixes.minByOrNull { it.length } ?: "" }
    }

    private fun isParadigmImplemented(word: Word?): Boolean =
        word?.paradigm in arrayListOf(DeclensionEngine.PARADIGM_KANTA, DeclensionEngine.PARADIGM_NADI)

    private fun enableOrDisableByMode(view: View, isEnabled: Boolean) {
        view.isEnabled = isEnabled
    }

    private fun toggleVisibilityByMode(view: View, isGone: Boolean) {
        view.isGone = isGone
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
                GrammaticalType.ADJECTIVE -> {
                    spinnerParadigm.visibility = View.VISIBLE
                    spinnerWordGender.visibility = View.GONE
                    spinnerVerbCase.visibility = View.GONE
                }
                GrammaticalType.NOUN, GrammaticalType.PROPER_NOUN -> {
                    spinnerParadigm.visibility = View.VISIBLE
                    spinnerWordGender.visibility = View.VISIBLE
                    spinnerVerbCase.visibility = View.GONE
                }
                GrammaticalType.VERB -> {
                    spinnerParadigm.visibility = View.GONE
                    spinnerWordGender.visibility = View.GONE
                    spinnerVerbCase.visibility = View.VISIBLE
                }
                else -> {
                    spinnerParadigm.visibility = View.GONE
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
        val paradigm = if(root.spinnerParadigm.isVisible) Paradigm.fromDescription(root.spinnerParadigm.selectedItem.toString()) else ""
        val verbClass = if(root.spinnerVerbCase.isVisible) VerbClass.toVerbClassFromName(root.spinnerVerbCase.selectedItem.toString()) else VerbClass.NONE
        val gender = if(root.spinnerWordGender.isVisible) converters.toGrammaticalGender(root.spinnerWordGender.selectedItem.toString()) else GrammaticalGender.NONE

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
                        modifyWord(it, newWord) {
                            oldWord = newWord
                            refreshUI()
                            toggleMode(root)
                        }
                    }
                }
            }
            else -> {
                Log.d(LOG_ADD_MODIFY, "onClickAdd() : No word")
                dialogManager.showErrorDialog(R.string.dialog_error_message_words_added)
            }
        }
    }

    private suspend fun modifyWord(oldWord: Word, newWord: Word, onSuccess: () -> Unit) {
        val result = wordsUpdateUseCase.updateWord(oldWord, newWord)
        if (result is WordsUpdateUseCase.Result.Success) {
            dialogManager.showInfoDialog(R.string.dialog_info_message_words_updated) {
                it.dismiss()
                onSuccess.invoke()
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

    // region declension/conjugation
    private fun setUpDeclensionFiltering(root: View) {
        root.recyclerViewDeclensions.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = DeclensionAdapter(requireActivity(), word).apply {
                onDeleteClick = {
                    // nothing
                }
                mViewMvcImpl = AddModifyWordViewMvcImpl(adapter = this) // todo remove adapter as a param when refactoring to arch complete
            }
            isGone = !shouldShowDeclension(oldWord)
        }
        root.editTextDetectDeclensionWord.apply {
            addTextChangedListener(object : TextWatcherImpl() {
                override fun afterTextChanged(s: Editable?) {
                    onDetectDeclension(s.toString())
                }
            })
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    onDetectDeclension(text.toString())
                }
            }
            isGone = !shouldShowDeclension(oldWord)
            requestFocus()

            onDetectDeclension(root.editTextDetectDeclensionWord.text.toString())
        }
    }

    private fun toggleDeclensionVisibility(root: View, word: Word?) {
        toggleVisibilityByMode(root.recyclerViewDeclensions, !shouldShowDeclension(word))
        root.editTextDetectDeclensionWord.apply {
            toggleVisibilityByMode(this, !shouldShowDeclension(word))
            if(this.isVisible) {
                this.requestFocus()
            }
        }
    }

    private fun toggleConjugationVisibility(root: View, word: Word?) {
        // todo
    }
    // endregion

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