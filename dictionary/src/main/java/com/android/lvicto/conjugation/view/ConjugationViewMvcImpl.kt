package com.android.lvicto.conjugation.view

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.R
import com.android.lvicto.common.textwatcher.BaseTextWatcher
import com.android.lvicto.common.constants.Constants.NONE
import com.android.lvicto.common.extention.initSpinner
import com.android.lvicto.common.view.BaseObservableMvc
import com.android.lvicto.conjugation.adapter.ConjugationAdapter
import com.android.lvicto.db.entity.Conjugation
import kotlinx.android.synthetic.main.fragment_conjugation.view.*

class ConjugationViewMvcImpl(
    val activity: AppCompatActivity,
    layouInflater: LayoutInflater,
    parent: ViewGroup?
) :
    BaseObservableMvc<ConjugationViewMvc.Listener>(), ConjugationViewMvc {

    init {
        setRootView(layouInflater.inflate(R.layout.fragment_conjugation, parent, false))
        init()
    }

    private val filterObserver: Observer<in Conjugation> = Observer { conjugation ->
        listeners.forEach { listener ->
            listener.onConjugationFilterAction(conjugation)
        }
    }

    private val LOG: String = "debconj"

    // todo can be move to a separate component ?
    private lateinit var conjugationMediatorLiveData: MediatorLiveData<Conjugation>
    private var paradigmRootLiveData: MutableLiveData<String> = MutableLiveData()
    private var endingLiveData: MutableLiveData<String> = MutableLiveData()
    private var verbClassLiveData: MutableLiveData<String> = MutableLiveData()
    private var numberLiveData: MutableLiveData<String> = MutableLiveData()
    private var personLiveData: MutableLiveData<String> = MutableLiveData()
    private var timeLiveData: MutableLiveData<String> = MutableLiveData()
    private var modeLiveData: MutableLiveData<String> = MutableLiveData()
    private var pardigmTypeLiveData: MutableLiveData<String> = MutableLiveData()
    private var formLiveDate: MutableLiveData<String> = MutableLiveData()

    private val NONE_CLASS: String = activity.resources.getStringArray(R.array.verb_class)[0]
    private val NONE_PERSON = activity.resources.getStringArray(R.array.grammatical_person)[0]
    private val NONE_NUMBER =
        activity.resources.getStringArray(R.array.filter_sanskrit_numbers_array)[0]
    private val NONE_TIME = activity.resources.getStringArray(R.array.verb_time)[0]
    private val NONE_MODE = activity.resources.getStringArray(R.array.verb_mode)[0]
    private val NONE_PARADYGM_TYPE =
        activity.resources.getStringArray(R.array.verb_paradigm_type)[0]


    lateinit var conjugation: Conjugation

    var isFiltering = false

    private fun init() {

        initLiveData()

        // region conjugation fields zone
        getRootView().edParadigmRoot?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s.toString().let {
                    val paradigmRoot = if (it.isEmpty()) {
                        NONE
                    } else {
                        it
                    }
                    conjugation.paradigmRoot = paradigmRoot
                    if (isFiltering) {
                        paradigmRootLiveData.value = paradigmRoot
                    }
                }
            }
        })
        getRootView().edEnding?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s.toString().let {
                    val ending = if (it.isEmpty()) {
                        NONE
                    } else {
                        it
                    }
                    conjugation.ending = ending
                    if (isFiltering) {
                        endingLiveData.value = ending
                    }
                }
            }
        })
        getRootView().spinnerClass?.initSpinner(R.array.verb_class, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbClass = if (it == NONE_CLASS) {
                    NONE
                } else {
                    it
                }
                conjugation.verbClass = verbClass
                if (isFiltering) {
                    verbClassLiveData.value = verbClass
                }
            }
        }, {
            verbClassLiveData.value = null // todo use the enum
        })
        getRootView().spinnerNumberVerb?.initSpinner(
            R.array.filter_sanskrit_numbers_array,
            { parent, position ->
                parent?.getItemAtPosition(position).toString().let {
                    val verbNumber = if (it == NONE_NUMBER) {
                        NONE
                    } else {
                        it
                    }
                    conjugation.verbNumber = verbNumber
                    if (isFiltering) {
                        numberLiveData.value = verbNumber
                    }
                }
            },
            {
                numberLiveData.value = null // todo use the enum
            })
        getRootView().spinnerPerson?.initSpinner(R.array.grammatical_person, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbPerson = if (it == NONE_PERSON) {
                    NONE
                } else {
                    it
                }
                conjugation.verbPerson = verbPerson
                if (isFiltering) {
                    personLiveData.value = verbPerson
                }
            }
        }, {
            personLiveData.value = null // todo use the enum
        })
        getRootView().spinnerTime?.initSpinner(R.array.verb_time, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbTime = if (it == NONE_TIME) {
                    NONE
                } else {
                    it
                }
                conjugation.verbTime = verbTime
                if (isFiltering) {
                    timeLiveData.value = verbTime
                }
            }
        }, {
            timeLiveData.value = null // todo use the enum
        })
        getRootView().spinnerMode?.initSpinner(R.array.verb_mode, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbMode = if (it == NONE_MODE) {
                    NONE
                } else {
                    it
                }
                conjugation.verbMode = verbMode
                if (isFiltering) {
                    modeLiveData.value = verbMode
                }
            }
        }, {
            modeLiveData.value = null // todo use the enum
        })
        getRootView().spinnerParadigmType?.initSpinner(
            R.array.verb_paradigm_type,
            { parent, position ->
                parent?.getItemAtPosition(position).toString().let {
                    val verbParadygmType = if (it == NONE_PARADYGM_TYPE) {
                        NONE
                    } else {
                        it
                    }
                    conjugation.verbParadygmType = verbParadygmType
                    if (isFiltering) {
                        pardigmTypeLiveData.value = verbParadygmType
                    }
                }
            },
            {
                pardigmTypeLiveData.value = null // todo use the enum
            })
        getRootView().radioFilter?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isFiltering = true
                getRootView().buttonAdd?.isEnabled = false
                conjugationMediatorLiveData.observe(activity, filterObserver)
                conjugationMediatorLiveData.value = conjugation.clone()
            }
        }
        getRootView().radioAdd?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isFiltering = false
                getRootView().buttonAdd?.isEnabled = true
                conjugationMediatorLiveData.removeObserver(filterObserver)
                listeners.forEach { listener ->
                    listener.onConjugationFilterAction(null) // reset filters to show all
                }
            }
        }
        getRootView().buttonAdd?.setOnClickListener {
            listeners.forEach { listener ->
                listener.onConjugationAddAction(conjugation)
            }
        }
        getRootView().buttonImport?.setOnClickListener {
            listeners.forEach { listener ->
                listener.onConjugationImport()
            }
        }
        getRootView().buttonExport?.setOnClickListener {
            listeners.forEach { listener ->
                listener.onConjugationExport()
            }
        }
        //  endregion

        // region detect
        getRootView().edFormToDecompose?.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                formLiveDate.value = s.toString()
            }
        })
        // endregion

        // region results
        initRecyclerView(getRootView())
        // endregion

    }


    private fun initLiveData() {
        conjugation = Conjugation(
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

        conjugationMediatorLiveData = MediatorLiveData()
        paradigmRootLiveData = MutableLiveData()
        endingLiveData = MutableLiveData()
        verbClassLiveData = MutableLiveData()
        numberLiveData = MutableLiveData()
        personLiveData = MutableLiveData()
        timeLiveData = MutableLiveData()
        modeLiveData = MutableLiveData()
        pardigmTypeLiveData = MutableLiveData()
        formLiveDate = MutableLiveData()

        conjugationMediatorLiveData.apply {
            this.addSource(paradigmRootLiveData) {
                conjugationMediatorLiveData.value = conjugation.clone()
            }
            this.addSource(endingLiveData) {
                conjugationMediatorLiveData.value = conjugation.clone()
            }
            this.addSource(verbClassLiveData) {
                conjugationMediatorLiveData.value = conjugation.clone()
            }
            this.addSource(numberLiveData) {
                conjugationMediatorLiveData.value = conjugation.clone()
            }
            this.addSource(personLiveData) {
                conjugationMediatorLiveData.value = conjugation.clone()
            }
            this.addSource(timeLiveData) {
                conjugationMediatorLiveData.value = conjugation.clone()
            }
            this.addSource(modeLiveData) {
                conjugationMediatorLiveData.value = conjugation.clone()
            }
            this.addSource(pardigmTypeLiveData) {
                conjugationMediatorLiveData.value = conjugation.clone()
            }

            formLiveDate.observe(activity, {
                listeners.forEach { listener ->
                    listener.onConjugationDetectAction(it)
                }
            })
        }
    }

    private fun initRecyclerView(root: View?) {
        root?.rvForms.apply {
            getContext().let { context ->
                this?.layoutManager = LinearLayoutManager(context)
                this?.adapter = ConjugationAdapter(context).apply {
                    this.onDeleteClick = { _conjugation ->
                        listeners.forEach { _listener ->
                            _listener.onConjugationDeleteAction(_conjugation)
                        }
                    }
                }
            }
        }
    }

    override fun setConjugations(conjugations: List<Conjugation>) {
        (getRootView().rvForms.adapter as ConjugationAdapter).refresh(conjugations)
        getRootView().tvConjugationCount.text = "${conjugations.size} forms"
    }

    override fun showProgress() {
        getRootView().progressBar.visibility = VISIBLE
    }

    override fun hideProgress() {
        getRootView().progressBar.visibility = GONE
    }

    override fun setFormRoot(formRoot: String) {
        getRootView().tvRootDetected.text = formRoot
    }

}