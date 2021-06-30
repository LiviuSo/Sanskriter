package com.android.lvicto.declension

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.R
import com.android.lvicto.common.adapter.DeclensionAdapter
import com.android.lvicto.db.data.Declensions
import com.android.lvicto.db.Converters
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.common.util.Constants
import com.android.lvicto.common.util.Constants.Dictionary.PICKFILE_RESULT_CODE
import com.android.lvicto.common.util.export
import com.android.lvicto.common.util.openFilePicker
import com.android.lvicto.words.WordsViewModel
import kotlinx.android.synthetic.main.activity_add_declension.*

class AddDeclensionActivity : AppCompatActivity() {

    private var mCase: String = "n/a"
    private var mGender: String = "n/a"
    private var mNumber: String = "n/a"

    private lateinit var mCaseFilter: MutableLiveData<String>
    private lateinit var mNumberFilter: MutableLiveData<String>
    private lateinit var mGenderFilter: MutableLiveData<String>
    private lateinit var mSuffixFilter: MutableLiveData<String>
    private lateinit var mEndingFilter: MutableLiveData<String>
    private lateinit var mParadigmFilter: MutableLiveData<String>

    private lateinit var viewModelDeclension: DeclensionViewModel
    private lateinit var viewModelWords: WordsViewModel
    private lateinit var adapter: DeclensionAdapter
    private lateinit var mDeclensionFilter: MutableLiveData<String>

    private val converters = Converters()

    private val exportObserver = Observer<List<Declension>> { declensions ->
        val filename = Constants.Dictionary.FILENAME_DECLENSION // todo make a constant for now
        if (declensions != null) {
            viewModelDeclension.writeToFile(Declensions(declensions), filename)
                .observe(this@AddDeclensionActivity, {
                    this.export(filename = filename)
                })
        }
    }

    private val importObserver = Observer<List<Declension>> {
        adapter.refresh(it)
    }

    private val filterObserver = Observer<String> {
        val declension: Declension = collectData()
        viewModelDeclension.filter(declension).observe(this, {
            adapter.refresh(it)
        })
    }

    private fun collectData(): Declension {
        val converters = Converters()

        return Declension(
            0,
            converters.toGramaticalCase(spinnerFilterCase.selectedItem.toString()),
            converters.toGramaticalNumber(spinnerFilterNumber.selectedItem.toString()),
            converters.toGramaticalGender(spinnerFilterGender.selectedItem.toString()),
            editTextFilterParadigm.text.toString(),
            editTextFilterEnding.text.toString(),
            editTextFilterSuffix.text.toString(),
            "" // not needed
        )
    }

    // first detect the declension(s),
    // then searching the roots in the dic that have that declension
    private val detectDeclensionObserver = Observer<String> { declensionWord ->
        viewModelDeclension.detectDeclension(declensionWord.toString())
            .observe(this, { declensions ->
                adapter.refresh(declensions) // todo remove when second recView in place
                declensions.distinctBy { declension ->
                    declension.suffix
                }.map { declension ->
                    if (declensionWord.isNotEmpty()) {
                        val sizeDeclWord = declensionWord.length
                        val root = "${
                            declensionWord.substring(0, sizeDeclWord - declension.suffix.length)
                        }${declension.paradigmEnding}"
                        viewModelWords.filter(root, declension.paradigm, true).observe(this, { dicRes ->
                            if (dicRes.isNotEmpty()) { // todo make a callback
                                tvResults.text = StringBuffer().let { sb ->
                                    dicRes.map {
                                        sb.append("${it.wordIAST} ${declension.gCase.abbr} ${declension.gNumber.abbr} ${declension.gGender.abbr}\n")
                                    }.first().toString()
                                }
                            } else {
                                tvResults.text = "No results yet."
                            }
                        })
                    } else {
                        tvResults.text = "No results yet."
                    }
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_declension)

        // todo injection
        viewModelDeclension = DeclensionViewModel(application)
        viewModelWords = WordsViewModel(application)

        setUpUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKFILE_RESULT_CODE) {
            data?.data?.let {
                viewModelDeclension.readFromFile(it)
                    .observe(this@AddDeclensionActivity, importObserver)
            } ?: Log.d(LOG_TAG, "path is null")
        }
    }

    private fun setUpUI() {

        // region livedata
        mCaseFilter = MutableLiveData<String>().apply {
            observe(this@AddDeclensionActivity, filterObserver)
        }
        mNumberFilter = MutableLiveData<String>().apply {
            observe(this@AddDeclensionActivity, filterObserver)
        }
        mGenderFilter = MutableLiveData<String>().apply {
            observe(this@AddDeclensionActivity, filterObserver)
        }
        mParadigmFilter = MutableLiveData<String>().apply {
            observe(this@AddDeclensionActivity, filterObserver)
        }
        mEndingFilter = MutableLiveData<String>().apply {
            observe(this@AddDeclensionActivity, filterObserver)
        }
        mSuffixFilter = MutableLiveData<String>().apply {
            observe(this@AddDeclensionActivity, filterObserver)
        }
        mDeclensionFilter = MutableLiveData<String>().apply {
            observe(this@AddDeclensionActivity, detectDeclensionObserver)
        }
        // endregion

        // region spinners add declension
        spinnerCase.apply {
            this.adapter = ArrayAdapter
                .createFromResource(
                    this@AddDeclensionActivity,
                    R.array.sanskrit_cases_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mCase = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mCase = "n/a"
                }

            }
        }

        spinnerNumber.apply {
            this.adapter = ArrayAdapter
                .createFromResource(
                    this@AddDeclensionActivity,
                    R.array.filter_sanskrit_numbers_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mNumber = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mNumber = "n/a"
                }

            }
        }

        spinnerGender.apply {
            this.adapter = ArrayAdapter
                .createFromResource(
                    this@AddDeclensionActivity,
                    R.array.sanskrit_gender_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mGender = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mGender = "n/a"
                }

            }
        }
        // endregion

        // region RV
        recyclerViewDeclensions.layoutManager = LinearLayoutManager(this)
        adapter = DeclensionAdapter(this).apply {
            this.onDeleteClick = { declension ->
                Log.d("decl11", "delete : $declension")
                viewModelDeclension.delete(declension).observe(this@AddDeclensionActivity, {
                    viewModelDeclension.getAll().observe(this@AddDeclensionActivity, { list ->
                        adapter.refresh(list)
                    })
                })
            }
        }
        recyclerViewDeclensions.adapter = adapter

        viewModelDeclension.getAll().observe(this, {
            adapter.refresh(it)
        })
        // endregion

        // region edit text
        editTextSuffix.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // nothing
            }

            override fun afterTextChanged(s: Editable?) {
                editTextParadigmDeclension.setText(
                    Declension.createDeclension(
                        editTextParadigm.text.toString(),
                        editTextParadigmEnding.text.toString(),
                        editTextSuffix.text.toString()
                    )
                )
            }
        })

        editTextFilterParadigm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mParadigmFilter.value = s.toString()
            }
        })

        editTextFilterEnding.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mEndingFilter.value = s.toString()
            }
        })

        editTextFilterSuffix.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mSuffixFilter.value = s.toString()
            }
        })

        editTextDetectDeclension.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mDeclensionFilter.value = s.toString()
            }
        })

        // endregion

        // region buttons
        buttonSave.setOnClickListener {
            viewModelDeclension.let { viewModel ->
                // insert into the DB
                val decl = Declension(
                    0,
                    converters.toGramaticalCase(mCase),
                    converters.toGramaticalNumber(mNumber),
                    converters.toGramaticalGender(mGender),
                    editTextParadigm.text.toString(),
                    editTextParadigmEnding.text.toString(),
                    editTextSuffix.text.toString(),
                    editTextParadigmDeclension.text.toString()
                )
                viewModel.insert(decl).observe(this, {
                    // refresh RV
                    viewModel.getAll().observe(this, {
                        adapter.refresh(it)
                    })
                })
            }
        }

        buttonImport.setOnClickListener {
            openFilePicker(PICKFILE_RESULT_CODE)
        }

        buttonExport.setOnClickListener {
            viewModelDeclension.getAll().observe(this, exportObserver)
        }
        // endregion

        // region spinners filter
        spinnerFilterCase.apply {
            this.adapter = ArrayAdapter
                .createFromResource(
                    this@AddDeclensionActivity,
                    R.array.filter_sanskrit_cases_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mCaseFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mCaseFilter.postValue("n/a")
                }
            }
        }

        spinnerFilterNumber.apply {
            this.adapter = ArrayAdapter
                .createFromResource(
                    this@AddDeclensionActivity,
                    R.array.filter_sanskrit_numbers_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mNumberFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mNumberFilter.postValue("n/a")
                }
            }
        }

        spinnerFilterGender.apply {
            this.adapter = ArrayAdapter
                .createFromResource(
                    this@AddDeclensionActivity,
                    R.array.filter_sanskrit_gender_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mGenderFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mGenderFilter.postValue("n/a")
                }
            }
        }
        // endregion
    }

    companion object {
        const val LOG_TAG = "AddDeclensionActivity"
    }

}