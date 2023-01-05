package com.android.lvicto.words.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.android.lvicto.R
import com.android.lvicto.common.Constants
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.ViewMvcImpl
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.*
import com.android.lvicto.dependencyinjection.Service
import kotlinx.android.synthetic.main.fragment_word_add.view.*

class WordDetailsViewImpl(val activity: BaseActivity,
                          val word: Word?,
                          private var mode: Int,
                          private val requestCode: Int,
                          containerView: ViewGroup?,
                          inflater: LayoutInflater
) : ViewMvcImpl<WordDetailsView.Listener>(), WordDetailsView {

    private var oldWord: Word

    @field:Service
    private lateinit var converters: Converters

    init {
        activity.injector.inject(this)
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

        setRootView(inflater.inflate(R.layout.fragment_word_add, containerView, false))
    }

    override fun buildUI() {
        val root = getRootView()
        root.apply {
            showModeIcon(this)
            ibEditLock.setOnClickListener {
                toggleMode(this)
            }

            editSa?.apply {
                setText(oldWord.wordSa)
                enableOrDisableByMode(this, mode == Constants.MODE_EDIT_WORD)
            }
            editIAST?.apply {
                setText(oldWord.wordIAST)
                enableOrDisableByMode(this, mode == Constants.MODE_EDIT_WORD)
            }
            editRo?.apply {
                setText(oldWord.meaningRo)
                enableOrDisableByMode(this, mode == Constants.MODE_EDIT_WORD)
            }
            editEn?.apply {
                setText(oldWord.meaningEn)
                enableOrDisableByMode(this, mode == Constants.MODE_EDIT_WORD)
            }

            spinnerParadigm.apply {
                adapter = ArrayAdapter.createFromResource(activity, R.array.paradigms, android.R.layout.simple_spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
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
                setSelection(Paradigm.getPosition(oldWord.paradigm))
                enableOrDisableByMode(this, mode == Constants.MODE_EDIT_WORD)
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
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
                setSelection(GrammaticalType.getPosition(oldWord.gType))
                enableOrDisableByMode(this, mode == Constants.MODE_EDIT_WORD)
            }
            spinnerWordGender.apply {
                adapter = ArrayAdapter.createFromResource(
                    activity,
                    R.array.filter_sanskrit_gender_array,
                    android.R.layout.simple_spinner_item
                )
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        word?.gender = converters.toGrammaticalGender(
                            parent?.getItemAtPosition(position).toString()
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        word?.gender = GrammaticalGender.NONE
                    }
                }
                setSelection(GrammaticalGender.getPosition(oldWord.gender))
                enableOrDisableByMode(root, mode == Constants.MODE_EDIT_WORD)
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
                setSelection(VerbClass.getPosition(oldWord.verbClass))
                enableOrDisableByMode(this, mode == Constants.MODE_EDIT_WORD)
            }

            showHideField(root, oldWord)

            btnSaveWord?.apply {
                setOnClickListener(this@WordDetailsViewImpl::onClickAdd)
                toggleVisibilityByMode(this, mode == Constants.MODE_VIEW_WORD)
            }
        }
    }

    override fun modifyWord(newWord: Word) {
        oldWord = newWord
        with(getRootView()) {
            buildUI()
            toggleMode(this)
        }
    }

    override fun registerListener(listener: WordDetailsView.Listener) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: WordDetailsView.Listener) {
        listeners.remove(listener)
    }

    private fun showModeIcon(root: View) {
        root.ibEditLock.background = if(mode == Constants.MODE_EDIT_WORD)
            activity.resources.getDrawable(R.drawable.ic_edit_24, null)
        else
            activity.resources.getDrawable(R.drawable.ic_lock_24, null)
    }

    private fun toggleMode(root: View) {
        mode = if(mode == Constants.MODE_EDIT_WORD)
            Constants.MODE_VIEW_WORD
        else
            Constants.MODE_EDIT_WORD

        showModeIcon(root)

        enableOrDisableByMode(root.editSa, mode == Constants.MODE_EDIT_WORD)
        enableOrDisableByMode(root.editIAST, mode == Constants.MODE_EDIT_WORD)
        enableOrDisableByMode(root.editRo, mode == Constants.MODE_EDIT_WORD)
        enableOrDisableByMode(root.editEn, mode == Constants.MODE_EDIT_WORD)
        enableOrDisableByMode(root.spinnerParadigm, mode == Constants.MODE_EDIT_WORD)
        enableOrDisableByMode(root.spinnerType, mode == Constants.MODE_EDIT_WORD)
        enableOrDisableByMode(root.spinnerWordGender, mode == Constants.MODE_EDIT_WORD)
        enableOrDisableByMode(root.spinnerVerbCase, mode == Constants.MODE_EDIT_WORD)

        toggleVisibilityByMode(root.btnSaveWord, mode == Constants.MODE_VIEW_WORD)
    }

    private fun enableOrDisableByMode(view: View, isEnabled: Boolean) {
        view.isEnabled = isEnabled
    }

    private fun toggleVisibilityByMode(view: View, isGone: Boolean) {
        view.isGone = isGone

        // toggle grammar container visibility
        if(mode == Constants.MODE_VIEW_WORD) { word } else { null }.also {
            for (l in listeners) {
                l.onShowWordGrammar(it)
            }
        }
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
            Constants.CODE_REQUEST_ADD_WORD -> {
                for(l in listeners) {
                    l.onAddWord(v, newWord)
                }
            }
            Constants.CODE_REQUEST_EDIT_WORD -> {
                for(l in listeners) {
                    l.onEditWord(oldWord, newWord)
                }
            }
            else -> {
                for(l in listeners) {
                    l.onAddWordError()
                }
            }
        }
    }

}