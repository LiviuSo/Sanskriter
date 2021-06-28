package com.android.lvicto.conjugation.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.android.lvicto.R
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.db.entity.Conjugation
import com.android.lvicto.common.dependencyinjection.Service
import com.android.lvicto.common.util.Constants
import com.android.lvicto.common.util.Constants.Dictionary.FILENAME_CONJUGATION
import com.android.lvicto.common.util.export
import com.android.lvicto.common.util.openFilePicker
import com.android.lvicto.conjugation.usecases.ConjugationAddUseCase
import com.android.lvicto.conjugation.usecases.ConjugationFetchUseCase
import com.android.lvicto.conjugation.usecases.ConjugationImportExportUseCase
import com.android.lvicto.conjugation.view.ConjugationViewMvc
import com.android.lvicto.ui.dialog.ConfirmationDialog
import com.android.lvicto.ui.dialog.ErrorDialog
import kotlinx.coroutines.*

class ConjugationActivity : BaseActivity(), ConjugationViewMvc.Listener {

    @field:Service
    private lateinit var mViewMvc: ConjugationViewMvc // todo injection

    @field:Service
    private lateinit var mConjugationAddUseCase: ConjugationAddUseCase

    @field:Service
    private lateinit var mConjugationFetchUseCase: ConjugationFetchUseCase

    @field:Service
    private lateinit var mConjugationImportExportUseCase: ConjugationImportExportUseCase

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        mViewMvc.init(R.layout.activity_conjugation)
    }

    override fun onStart() {
        super.onStart()
        mViewMvc.registerListener(this)

        if (!mViewMvc.isFiltering) {
            filter(null)
        } // to init the RecView
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
        mViewMvc.unregisterListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.Dictionary.PICKFILE_RESULT_CODE) {
            data?.data?.let {
                loadImports(it)
            }
        }
    }

    // region Listener
    override fun onConjugationAddAction(conjugation: Conjugation?) {
        add(conjugation)
    }

    override fun onConjugationDeleteAction(conjugation: Conjugation?) {
        delete(conjugation)
    }

    override fun onConjugationFilterAction(conjugation: Conjugation?) {
        filter(conjugation)
    }

    override fun onConjugationDetectAction(form: String) {
        detect(form)
    }

    override fun onConjugationImport() {
        import()
    }

    override fun onConjugationExport() {
        export()
    }
    // endregion

    // region private
    private fun add(conjugation: Conjugation?) {
        coroutineScope.launch {
            if (conjugation != null) {
                mViewMvc.showProgress()
                try {
                    val resAdd = mConjugationAddUseCase.addConjugation(conjugation)
                    if (resAdd is ConjugationAddUseCase.Result.Success) {
                        val resFetch = mConjugationFetchUseCase.filter()
                        if (resFetch is ConjugationFetchUseCase.Result.Success) {
                            mViewMvc.setConjugations(resFetch.conjugations)
                        } else {
                            mViewMvc.showErrorDialog("Fail to filter") {
                                // onRetry() empty for now
                            }
                        }
                    } else {
                        mViewMvc.showErrorDialog("Fail to add") {
                            // onRetry() empty for now
                        }
                    }
                } catch (e: Exception) {
                    mViewMvc.showErrorDialog("Unknown error (add): ${e.message}") {
                        // onRetry() empty for now
                    }
                } finally {
                    mViewMvc.hideProgress()
                }
            }
        }
    }

    private fun filter(conjugation: Conjugation?) {
        coroutineScope.launch {
            mViewMvc.showProgress()
            try {
                val res = mConjugationFetchUseCase.filter(conjugation)
                if (res is ConjugationFetchUseCase.Result.Success) {
                    mViewMvc.setConjugations(res.conjugations)
                } else {
                    mViewMvc.showErrorDialog("Fail to filter") {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                mViewMvc.showErrorDialog("Unknown error (filter): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvc.hideProgress()
            }
        }
    }

    private fun delete(conjugation: Conjugation?) {
        conjugation?.let { ConfirmationDialog(this, it, ::deleteDialogAction).showDialog() }
    }

    private fun deleteDialogAction(conjugation: Conjugation) {
        coroutineScope.launch {
            mViewMvc.showProgress()
            try {
                val resAdd = mConjugationAddUseCase.delete(listOf(conjugation))
                if (resAdd is ConjugationAddUseCase.Result.Success) {
                    val filterBy = if (mViewMvc.isFiltering) {
                        mViewMvc.conjugation
                    } else {
                        null
                    }
                    val resFetch = mConjugationFetchUseCase.filter(filterBy)
                    if (resFetch is ConjugationFetchUseCase.Result.Success) {
                        mViewMvc.setConjugations(resFetch.conjugations)
                    } else {
                        mViewMvc.showErrorDialog("Fail to filter") {
                            // onRetry() empty for now
                        }
                    }
                }
            } catch (e: Exception) {
                mViewMvc.showErrorDialog("Unknown error (delete): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvc.hideProgress()
            }
        }
    }

    private fun export() {
        coroutineScope.launch {
            try {
                mViewMvc.showProgress()
                val conjugations = mConjugationFetchUseCase.filter()
                if (conjugations is ConjugationFetchUseCase.Result.Success) {
                    val res = mConjugationImportExportUseCase.exportConjugations(
                        conjugations.conjugations,
                        FILENAME_CONJUGATION
                    )
                    if (res is ConjugationImportExportUseCase.Result.SuccessWrite) {
                        this@ConjugationActivity.export(
                            FILENAME_CONJUGATION,
                            "Dictionary: exporting conjugations"
                        )
                    } else {
                        mViewMvc.showErrorDialog("Fail to export") {
                            // onRetry() empty for now
                        }
                    }
                } else {
                    mViewMvc.showErrorDialog("Fail to filter") {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                mViewMvc.showErrorDialog("Unknown error (export): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvc.hideProgress()
            }
        }
    }

    private fun import() {
        openFilePicker(Constants.Dictionary.PICKFILE_RESULT_CODE)
    }

    private fun loadImports(uri: Uri) {
        coroutineScope.launch {
            try {
                mViewMvc.showProgress()
                val res = mConjugationImportExportUseCase.importConjugations(uri)
                if (res is ConjugationImportExportUseCase.Result.SuccessRead) {
                    if (res.conjugations != null) {
                        mConjugationAddUseCase.addConjugations(res.conjugations)
                        mViewMvc.setConjugations(res.conjugations)
                    } else {
                        mViewMvc.showErrorDialog("Fail to add conjugations.") {
                            // onRetry() empty for now
                        }
                    }
                } else {
                    mViewMvc.showErrorDialog("Fail to import") {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                mViewMvc.showErrorDialog("Unknown error (import): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvc.hideProgress()
            }
        }
    }

    private fun detect(form: String) {
        coroutineScope.launch {
            try {
                mViewMvc.showProgress()
                if (form.isNotEmpty()) { // try to detect
                    var conjugations: List<Conjugation> = arrayListOf()
                    val len = form.length
                    var index = len - 1
                    var hasResults: Boolean
                    var result: ConjugationFetchUseCase.Result
                    var isDetected = false
                    scan@ while (index >= 0) {
                        val ending = form.substring(index, len)
                        result = mConjugationFetchUseCase.filterByEnding(ending)
                        hasResults = if (result is ConjugationFetchUseCase.Result.Success) {
                            result.conjugations.isNotEmpty()
                        } else {
                            ErrorDialog(this@ConjugationActivity, "Unable to detect form") {}
                            false
                        }
                        if (!hasResults) { // we reached a point where there are no results
                            // was there only one result previously ?

                            isDetected = conjugations.size == 1
                                    && conjugations.first().ending == form.substring(
                                index + 1,
                                len
                            ) // compare previous ending candidate
                            break@scan
                        } else { // continue toward the beginning
                            conjugations =
                                (result as ConjugationFetchUseCase.Result.Success).conjugations
                            isDetected = false
                            index--
                        }
                    } // end scan from the end loop

                    if (isDetected) {
                        mViewMvc.setConjugations(conjugations)
                        val root = form.substring(0, index + 1)
                        mViewMvc.setFormRoot(root)
                    } else {
                        mViewMvc.setConjugations(arrayListOf())
                        mViewMvc.setFormRoot("") // reset result
                    }
                } else { // reset
                    val res = mConjugationFetchUseCase.filter()
                    if (res is ConjugationFetchUseCase.Result.Success) {
                        mViewMvc.setConjugations(res.conjugations)
                    } else {
                        ErrorDialog(
                            this@ConjugationActivity,
                            "Unknown exception. Unable to detect form"
                        ) {}
                    }
                }
            } catch (e: Exception) {
                ErrorDialog(
                    this@ConjugationActivity,
                    "Unknown exception. Unable to detect form"
                ) {}
            } finally {
                mViewMvc.hideProgress()
            }
        }
    }

// endregion
}