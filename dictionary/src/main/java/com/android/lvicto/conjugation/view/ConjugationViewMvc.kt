package com.android.lvicto.conjugation.view

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.R
import com.android.lvicto.conjugation.adapter.ConjugationAdapter
import com.android.lvicto.db.entity.Conjugation
import com.android.lvicto.common.util.Constants.Dictionary.NONE
import com.android.lvicto.common.util.initSpinner
import com.android.lvicto.common.view.BaseViewMvs
import com.android.lvicto.ui.dialog.ErrorDialog
import kotlinx.android.synthetic.main.activity_conjugation.*

class ConjugationViewMvc(val activity: AppCompatActivity)
    : BaseViewMvs<ConjugationViewMvc.Listener>() {

    interface Listener {
        fun onConjugationAddAction(conjugation: Conjugation?)
        fun onConjugationDeleteAction(conjugation: Conjugation?)
        fun onConjugationFilterAction(conjugation: Conjugation?)
        fun onConjugationDetectAction(form: String)
        fun onConjugationImport()
        fun onConjugationExport()
    }

    private val filterObserver: Observer<in Conjugation> = Observer { conjugation ->
        listeners.forEach { listener ->
            listener.onConjugationFilterAction(conjugation)
        }
    }

    private val LOG: String = "debconj"

    // todo can be move to a separate component ?
    private val conjugationMediatorLiveData: MediatorLiveData<Conjugation> = MediatorLiveData()
    private val paradigmRootLiveData: MutableLiveData<String> = MutableLiveData()
    private val endingLiveData: MutableLiveData<String> = MutableLiveData()
    private val verbClassLiveData: MutableLiveData<String> = MutableLiveData()
    private val numberLiveData: MutableLiveData<String> = MutableLiveData()
    private val personLiveData: MutableLiveData<String> = MutableLiveData()
    private val timeLiveData: MutableLiveData<String> = MutableLiveData()
    private val modeLiveData: MutableLiveData<String> = MutableLiveData()
    private val pardigmTypeLiveData: MutableLiveData<String> = MutableLiveData()
    private val formLiveDate: MutableLiveData<String> = MutableLiveData()

    private val NONE_CLASS: String = activity.resources.getStringArray(R.array.verb_class)[0]
    private val NONE_PERSON = activity.resources.getStringArray(R.array.grammatical_person)[0]
    private val NONE_NUMBER = activity.resources.getStringArray(R.array.filter_sanskrit_numbers_array)[0]
    private val NONE_TIME = activity.resources.getStringArray(R.array.verb_time)[0]
    private val NONE_MODE = activity.resources.getStringArray(R.array.verb_mode)[0]
    private val NONE_PARADYGM_TYPE = activity.resources.getStringArray(R.array.verb_paradigm_type)[0]


    var conjugation: Conjugation? = Conjugation(
        0,
        NONE,
        NONE,
        null,
        null,
        null,
        null,
        null,
        null
    )

    var isFiltering = false

    fun init(@LayoutRes layoutId: Int) {
        activity.setContentView(layoutId)

        initLiveData()

        // region conjugation fields zone
        activity.edParadigmRoot.addTextChangedListener(object : TextWatcherOnChange() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s.toString().let {
                    val paradigmRoot = if(it.isEmpty()) {
                        NONE
                    } else {
                        it
                    }
                    conjugation?.paradigmRoot = paradigmRoot
                    if(isFiltering) {
                        paradigmRootLiveData.value = paradigmRoot
                    }
                }
            }
        })
        activity.edEnding.addTextChangedListener(object : TextWatcherOnChange() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s.toString().let {
                    val ending = if(it.isEmpty()) {
                        NONE
                    } else {
                        it
                    }
                    conjugation?.ending = ending
                    if(isFiltering) {
                        endingLiveData.value = ending
                    }
                }
            }
        })
        activity.spinnerClass.initSpinner(R.array.verb_class, { parent,  position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbClass = if(it == NONE_CLASS) {
                    NONE
                } else {
                    it
                }
                conjugation?.verbClass = verbClass
                if(isFiltering) {
                    verbClassLiveData.value = verbClass
                }
            }
        }, {
            verbClassLiveData.value = null // todo use the enum
        })
        activity.spinnerNumberVerb.initSpinner(R.array.filter_sanskrit_numbers_array, { parent,  position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbNumber = if(it == NONE_NUMBER) {
                    NONE
                } else {
                    it
                }
                conjugation?.verbNumber = verbNumber
                if(isFiltering) {
                    numberLiveData.value = verbNumber
                }
            }
        }, {
            numberLiveData.value = null // todo use the enum
        })
        activity.spinnerPerson.initSpinner(R.array.grammatical_person, { parent,  position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbPerson = if(it == NONE_PERSON) {
                    NONE
                } else {
                    it
                }
                conjugation?.verbPerson = verbPerson
                if(isFiltering) {
                    personLiveData.value = verbPerson
                }
            }
        }, {
            personLiveData.value = null // todo use the enum
        })
        activity.spinnerTime.initSpinner(R.array.verb_time, { parent,  position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbTime = if(it == NONE_TIME) {
                    NONE
                } else {
                    it
                }
                conjugation?.verbTime = verbTime
                if(isFiltering) {
                    timeLiveData.value = verbTime
                }
            }
        }, {
            timeLiveData.value = null // todo use the enum
        })
        activity.spinnerMode.initSpinner(R.array.verb_mode, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbMode = if(it == NONE_MODE) {
                    NONE
                } else {
                    it
                }
                conjugation?.verbMode = verbMode
                if(isFiltering) {
                    modeLiveData.value = verbMode
                }
            }
        }, {
            modeLiveData.value = null // todo use the enum
        })
        activity.spinnerParadigmType.initSpinner(R.array.verb_paradigm_type, { parent,  position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbParadygmType = if(it == NONE_PARADYGM_TYPE) {
                    NONE
                } else {
                    it
                }
                conjugation?.verbParadygmType = verbParadygmType
                if(isFiltering) {
                    pardigmTypeLiveData.value = verbParadygmType
                }
            }
        }, {
            pardigmTypeLiveData.value = null // todo use the enum
        })
        activity.radioFilter.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isFiltering = true
                activity.buttonAdd.isEnabled = false
                conjugationMediatorLiveData.observe(activity, filterObserver)
                conjugationMediatorLiveData.value = conjugation?.clone()
            }
        }
        activity.radioAdd.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isFiltering = false
                activity.buttonAdd.isEnabled = true
                conjugationMediatorLiveData.removeObserver(filterObserver)
                listeners.forEach { listener ->
                    listener.onConjugationFilterAction(null) // reset filters to show all
                }
            }
        }
        activity.buttonAdd.setOnClickListener {
            listeners.forEach { listener ->
                listener.onConjugationAddAction(conjugation)
            }
        }
        activity.buttonImport.setOnClickListener {
            listeners.forEach { listener ->
                listener.onConjugationImport()
            }
        }
        activity.buttonExport.setOnClickListener {
            listeners.forEach { listener ->
                listener.onConjugationExport()
            }
        }
        //  endregion

        // region detect
        activity.edFormToDecompose.addTextChangedListener(object : TextWatcherOnChange() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                formLiveDate.value = s.toString()
            }
        })
        // endregion

        // region results
        initRecylerView()
        // endregion
    }

    private fun initLiveData() {
        conjugationMediatorLiveData.apply {
            this.addSource(paradigmRootLiveData) {
                conjugationMediatorLiveData.value = conjugation?.clone()
            }
            this.addSource(endingLiveData) {
                conjugationMediatorLiveData.value = conjugation?.clone()
            }
            this.addSource(verbClassLiveData) {
                conjugationMediatorLiveData.value = conjugation?.clone()
            }
            this.addSource(numberLiveData) {
                conjugationMediatorLiveData.value = conjugation?.clone()
            }
            this.addSource(personLiveData) {
                conjugationMediatorLiveData.value = conjugation?.clone()
            }
            this.addSource(timeLiveData) {
                conjugationMediatorLiveData.value = conjugation?.clone()
            }
            this.addSource(modeLiveData) {
                conjugationMediatorLiveData.value = conjugation?.clone()
            }
            this.addSource(pardigmTypeLiveData) {
                conjugationMediatorLiveData.value = conjugation?.clone()
            }

            formLiveDate.observe(activity, {
                listeners.forEach {
                    listener -> listener.onConjugationDetectAction(it)
                }
            })
        }
    }

    private fun initRecylerView() {
        activity.rvForms.apply {
            this.layoutManager = LinearLayoutManager(activity)
            this.adapter  =  ConjugationAdapter(context).apply {
                this.onDeleteClick = { _conjugation ->
                    listeners.forEach { _listener ->
                        _listener.onConjugationDeleteAction(_conjugation)
                    }
                }
            }
        }
    }

    fun setConjugations(conjugations: List<Conjugation>) {
        Log.d(LOG, "setConjugations: $conjugations")
        (activity.rvForms.adapter as ConjugationAdapter).refresh(conjugations)
    }

    fun showProgress() {
        activity.progressBar.visibility = VISIBLE
    }

    fun hideProgress() {
        activity.progressBar.visibility = GONE
    }

    fun showErrorDialog(message: String, onRetry: () -> Unit) {
        ErrorDialog(activity, message, onRetry).showDialog()
    }

    abstract class TextWatcherOnChange : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // nothing
        }

        override fun afterTextChanged(s: Editable?) {
            // nothing
        }
    }
}