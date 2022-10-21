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
import com.android.lvicto.common.base.BaseTextWatcher
import com.android.lvicto.db.Converters
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.declension.adapter.DeclensionAdapter
import com.android.lvicto.declension.view.interf.DeclensionsViewMvc
import kotlinx.android.synthetic.main.fragment_declension.view.*
import com.android.lvicto.common.base.BaseObservableMvc as BaseObservableMvc1

class DeclensionsViewMvcImpl(private val mActivity: BaseActivity, )
    : BaseObservableMvc1<DeclensionsViewMvc.Listener>(), DeclensionsViewMvc {

    private var mCase: String = "n/a"
    private var mGender: String = "n/a"
    private var mNumber: String = "n/a"

    private lateinit var mCaseFilter: MutableLiveData<String>
    private lateinit var mNumberFilter: MutableLiveData<String>
    private lateinit var mGenderFilter: MutableLiveData<String>
    private lateinit var mSuffixFilter: MutableLiveData<String>
    private lateinit var mEndingFilter: MutableLiveData<String>
    private lateinit var mParadigmFilter: MutableLiveData<String>

    private lateinit var mDeclensionFilter: MutableLiveData<String>

    private val converters = Converters()

    private lateinit var adapter: DeclensionAdapter


    override fun setUpUI(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        @LayoutRes layoutId: Int
    ): View {
        val root = layoutInflater.inflate(layoutId, parent, false)

        // region livedata
        requireActivity().let { activity ->
            mCaseFilter = MutableLiveData<String>().apply {
                observe(activity) {
                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            mNumberFilter = MutableLiveData<String>().apply {
                observe(activity) {
                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            mGenderFilter = MutableLiveData<String>().apply {
                observe(
                    activity,
                ) {
                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            mParadigmFilter = MutableLiveData<String>().apply {
                observe(activity) {

                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            mEndingFilter = MutableLiveData<String>().apply {
                observe(activity) {

                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            mSuffixFilter = MutableLiveData<String>().apply {
                observe(activity) {
                    listeners.forEach { listener ->
                        listener.onFilter()
                    }
                }
            }
            mDeclensionFilter = MutableLiveData<String>().apply {
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
                    mCase = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mCase = "n/a"
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
                    mNumber = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mNumber = "n/a"
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
                    mGender = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mGender = "n/a"
                }

            }
        }
        // endregion

        // region RV
        root.recyclerViewDeclensions.layoutManager = LinearLayoutManager(requireActivity())
        adapter = DeclensionAdapter(requireActivity()).apply {
            this@apply.onDeleteClick = { declension ->
                (this@DeclensionsViewMvcImpl).listeners.forEach {
                    it.onDeleteDeclension(declension)
                }
            }
        }
        root.recyclerViewDeclensions.adapter = adapter
        // endregion

        // region edit text
        root.editTextSuffix.addTextChangedListener( object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                root.editTextParadigmDeclension.setText(
                    Declension.createDeclension(
                        root.editTextParadigm.text.toString(),
                        root.editTextSuffix.text.toString()
                    )
                )
            }
        })

        root.editTextFilterParadigm.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                mParadigmFilter.value = s.toString()
            }
        })

        root.editTextFilterEnding.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                mEndingFilter.value = s.toString()
            }
        })

        root.editTextFilterSuffix.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                mSuffixFilter.value = s.toString()
            }
        })

        root.editTextDetectDeclension.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                mDeclensionFilter.value = s.toString()
            }
        })

        // endregion

        // region buttons
        root.buttonSave.setOnClickListener {
            listeners.forEach { listener ->
                listener.onSaveDeclensions(
                    Declension(
                        0,
                        converters.toGrammaticalCase(mCase),
                        converters.toGrammaticalNumber(mNumber),
                        converters.toGrammaticalGender(mGender),
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
                    mCaseFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mCaseFilter.postValue("n/a")
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
                    mNumberFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mNumberFilter.postValue("n/a")
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
                    mGenderFilter.postValue(parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    mGenderFilter.postValue("n/a")
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

    // endregion
}