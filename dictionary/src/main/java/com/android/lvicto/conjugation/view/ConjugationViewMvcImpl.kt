package com.android.lvicto.conjugation.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.R
import com.android.lvicto.common.Constants.NONE
import com.android.lvicto.common.extention.initSpinner
import com.android.lvicto.common.base.BaseTextWatcher
import com.android.lvicto.common.base.BaseObservableMvc
import com.android.lvicto.conjugation.adapter.ConjugationAdapter
import com.android.lvicto.db.entity.Conjugation
import kotlinx.android.synthetic.main.fragment_conjugation.view.*

class ConjugationViewMvcImpl(
    val activity: AppCompatActivity,
    layouInflater: LayoutInflater,
    parent: ViewGroup?
) : BaseObservableMvc<ConjugationViewMvc.Listener>(), ConjugationViewMvc {

    private val filterObserver: Observer<in Conjugation> = Observer { conjugation ->
        if (isFiltering) {
            Log.d(DEBUG_TAG, "filtering by $conjugation")
            listeners.forEach { listener ->
                listener.onConjugationFilterAction(conjugation)
            }
        } else {
            Log.d(DEBUG_TAG, "no filtering")
        }
    }

    init {
        setRootView(layouInflater.inflate(R.layout.fragment_conjugation, parent, false))
        init()
    }

    private val noneClass: String = activity.resources.getStringArray(R.array.verb_class)[0]
    private val nonePerson = activity.resources.getStringArray(R.array.grammatical_person)[0]
    private val noneNumber =
        activity.resources.getStringArray(R.array.filter_sanskrit_numbers_array)[0]
    private val noneTime = activity.resources.getStringArray(R.array.verb_time)[0]
    private val noneMode = activity.resources.getStringArray(R.array.verb_mode)[0]
    private val noneParadigmType =
        activity.resources.getStringArray(R.array.verb_paradigm_type)[0]


    internal var conjugation: Conjugation = Conjugation(
        id = -1,
        paradigmRoot = "",
        ending = "",
        verbClass = "",
        verbNumber = "",
        verbPerson = "",
        verbTime = "",
        verbMode = "",
        verbParadygmType = ""
    )

    var isFiltering = false

    private fun init() {

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
                        filter(conjugation)
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
                        filter(conjugation)
                    }
                }
            }
        })
        getRootView().spinnerClass?.initSpinner(R.array.verb_class, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbClass = if (it == noneClass) {
                    NONE
                } else {
                    it
                }
                conjugation.verbClass = verbClass
                if (isFiltering) {
                    Log.d(DEBUG_TAG, "isFilering=rue verbClass=$verbClass")
                    filter(conjugation)
                }
            }
        }, {
            filter(null)
        })
        getRootView().spinnerNumberVerb?.initSpinner(
            R.array.filter_sanskrit_numbers_array,
            { parent, position ->
                parent?.getItemAtPosition(position).toString().let {
                    val verbNumber = if (it == noneNumber) {
                        NONE
                    } else {
                        it
                    }
                    conjugation.verbNumber = verbNumber
                    if (isFiltering) {
                        filter(conjugation)
                    }
                }
            },
            {
                filter(null)
            })
        getRootView().spinnerPerson?.initSpinner(R.array.grammatical_person, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbPerson = if (it == nonePerson) {
                    NONE
                } else {
                    it
                }
                conjugation.verbPerson = verbPerson
                if (isFiltering) {
                    filter(conjugation)
                }
            }
        }, {
            filter(null)
        })
        getRootView().spinnerTime?.initSpinner(R.array.verb_time, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbTime = if (it == noneTime) {
                    NONE
                } else {
                    it
                }
                conjugation.verbTime = verbTime
                if (isFiltering) {
                    filter(conjugation)
                }
            }
        }, {
            filter(null)
        })
        getRootView().spinnerMode?.initSpinner(R.array.verb_mode, { parent, position ->
            parent?.getItemAtPosition(position).toString().let {
                val verbMode = if (it == noneMode) {
                    NONE
                } else {
                    it
                }
                conjugation.verbMode = verbMode
                if (isFiltering) {
                    filter(conjugation)
                }
            }
        }, {
            filter(null)
        })
        getRootView().spinnerParadigmType?.initSpinner(
            R.array.verb_paradigm_type,
            { parent, position ->
                parent?.getItemAtPosition(position).toString().let {
                    val verbParadygmType = if (it == noneParadigmType) {
                        NONE
                    } else {
                        it
                    }
                    conjugation.verbParadygmType = verbParadygmType
                    if (isFiltering) {
                        filter(conjugation)
                    }
                }
            },
            {
                filter(null)
            })
        getRootView().radioFilter?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isFiltering = true
                getRootView().buttonAdd?.isEnabled = false
                filter(conjugation)
            }
        }
        getRootView().radioAdd?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isFiltering = false
                getRootView().buttonAdd?.isEnabled = true
                filter(null)
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
//                formLiveDate.value = s.toString()
                for (listener in listeners) {
                    listener.onConjugationDetectAction(s.toString())
                }
            }
        })
        // endregion

        // region results
        initRecyclerView(getRootView())
        // endregion
    }

    private fun filter(conjugation: Conjugation?) {
        for (listener in listeners) {
            listener.onConjugationFilterAction(conjugation)
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

    companion object {
        private const val DEBUG_TAG: String = "debug_conj"
    }

}