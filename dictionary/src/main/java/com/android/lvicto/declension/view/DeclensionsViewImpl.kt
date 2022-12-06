package com.android.lvicto.declension.view

import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.R
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.TextWatcherImpl
import com.android.lvicto.common.base.ViewMvcImpl
import com.android.lvicto.db.Converters
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.declension.adapter.DeclensionAdapter
import kotlinx.android.synthetic.main.fragment_declension.view.*

class DeclensionsViewImpl(private val mActivity: BaseActivity) : ViewMvcImpl<DeclensionsView.Listener>(), DeclensionsView {

    private var case: String = "n/a"
    private var gender: String = "n/a"
    private var number: String = "n/a"

    private lateinit var caseFilter: MutableLiveData<String>
    private lateinit var numberFilter: MutableLiveData<String>
    private lateinit var genderFilter: MutableLiveData<String>
    private lateinit var suffixFilter: MutableLiveData<String>
    private lateinit var endingFilter: MutableLiveData<String>
    private lateinit var paradigmFilter: MutableLiveData<String>

    private lateinit var declensionFilter: MutableLiveData<String>
    private lateinit var adapter: DeclensionAdapter

    private val converters = Converters()

    override fun setUpUI(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        @LayoutRes layoutId: Int
    ): View {
        val root = layoutInflater.inflate(layoutId, parent, false)

        // region livedata
        requireActivity().let { activity ->
            caseFilter = MutableLiveData<String>().apply {
                observe(activity) {
                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            numberFilter = MutableLiveData<String>().apply {
                observe(activity) {
                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            genderFilter = MutableLiveData<String>().apply {
                observe(
                    activity,
                ) {
                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            paradigmFilter = MutableLiveData<String>().apply {
                observe(activity) {

                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            endingFilter = MutableLiveData<String>().apply {
                observe(activity) {

                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            suffixFilter = MutableLiveData<String>().apply {
                observe(activity) {
                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            declensionFilter = MutableLiveData<String>().apply {
                observe(activity) {
                    listeners.forEach { listener ->
                        listener.onDetectDeclension(it)
                    }
                }
            }
        }
        // endregion

        // region spinners add declension
        root.spinnerCase.apply {
            this@apply.adapter = ArrayAdapter
                .createFromResource(
                    requireActivity(),
                    R.array.sanskrit_cases_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this@apply.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    case = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    case = "n/a"
                }
            }
        }

        root.spinnerNumber.apply {
            this@apply.adapter = ArrayAdapter
                .createFromResource(
                    requireActivity(),
                    R.array.filter_sanskrit_numbers_array,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this@apply.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    number = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    number = "n/a"
                }
            }
        }

        root.spinnerGender.apply {
            this@apply.adapter = ArrayAdapter
                .createFromResource(
                    requireActivity(),
                    R.array.sanskrit_gender_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this@apply.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    gender = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    gender = "n/a"
                }

            }
        }
        // endregion

        // region RV
        root.recyclerViewDeclensions.layoutManager = LinearLayoutManager(requireActivity())
        adapter = DeclensionAdapter(requireActivity()).apply {
            this@apply.onDeleteClick = { declension ->
                (this@DeclensionsViewImpl).listeners.forEach {
                    it.onDeleteDeclension(declension)
                }
            }
        }
        root.recyclerViewDeclensions.adapter = adapter
        // endregion

        // region edit text
        root.editTextSuffix.addTextChangedListener( object : TextWatcherImpl() {
            override fun afterTextChanged(s: Editable?) {
                val endingLength = root.editTextParadigmEnding.text.toString().length
                val paradigmRoot = root.editTextParadigm.text.toString().dropLast(endingLength)
                root.editTextParadigmDeclension.setText(Declension.createDeclension(paradigmRoot, root.editTextSuffix.text.toString()))
            }
        })

        root.editTextFilterParadigm.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(s: Editable?) {
                paradigmFilter.value = s.toString()
            }
        })

        root.editTextFilterEnding.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(s: Editable?) {
                endingFilter.value = s.toString()
            }
        })

        root.editTextFilterSuffix.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(s: Editable?) {
                suffixFilter.value = s.toString()
            }
        })

        root.editTextDetectDeclension.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(s: Editable?) {
                declensionFilter.value = s.toString()
            }
        })

        // endregion

        // region buttons
        root.buttonSave.setOnClickListener {
            listeners.forEach { listener ->
                val endingLength = root.editTextParadigmEnding.text.toString().length
                val paradigmRoot = root.editTextParadigm.text.toString().dropLast(endingLength)
                root.editTextParadigmDeclension.setText(Declension.createDeclension(paradigmRoot, root.editTextSuffix.text.toString()))

                listener.onSaveDeclensions(
                    Declension(
                        0,
                        converters.toGrammaticalCase(case),
                        converters.toGrammaticalNumber(number),
                        converters.toGrammaticalGender(gender),
                        root.editTextParadigm.text.toString(),
                        root.editTextParadigmEnding.text.toString(),
                        root.editTextSuffix.text.toString(),
                        root.editTextParadigmDeclension.text.toString()
                    )
                )
            }
        }

        root.buttonImport.setOnClickListener {
            Log.d("Declension_log", "on declension import")
            listeners.forEach { listener ->
                listener.onButtonImport()
            }
        }

        root.buttonExport.setOnClickListener {
            listeners.forEach { listener ->
                listener.onButtonExport()
            }
        }
        // endregion

        // region spinners filter
        root.spinnerFilterCase.apply {
            this@apply.adapter = ArrayAdapter
                .createFromResource(
                    requireActivity(),
                    R.array.filter_sanskrit_cases_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this@apply.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    caseFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    caseFilter.postValue("n/a")
                }
            }
        }

        root.spinnerFilterNumber.apply {
            this@apply.adapter = ArrayAdapter
                .createFromResource(
                    requireActivity(),
                    R.array.filter_sanskrit_numbers_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this@apply.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    numberFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    numberFilter.postValue("n/a")
                }
            }
        }

        root.spinnerFilterGender.apply {
            this@apply.adapter = ArrayAdapter
                .createFromResource(
                    requireActivity(),
                    R.array.filter_sanskrit_gender_array,
                    android.R.layout.simple_spinner_item
                )
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            this@apply.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    genderFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    genderFilter.postValue("n/a")
                }
            }
        }
        // endregion

        return root
    }

    override fun setDeclensions(declensions: List<Declension>) {
        adapter.refresh(declensions) // todo remove when second recView in place
    }

    // region private
    override fun requireActivity(): BaseActivity = mActivity

    override fun registerListener(listener: DeclensionsView.Listener) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: DeclensionsView.Listener) {
        listeners.remove(listener)
    }

    // endregion
}