package com.android.lvicto.conjugation.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.common.ImportPickerCodeHolder
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.fragment.BaseFragment
import com.android.lvicto.common.constants.Constants
import com.android.lvicto.common.constants.Constants.RESULT_CODE_PICKFILE_CONJUGATIONS
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.extention.export
import com.android.lvicto.common.extention.openFilePicker
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.common.view.factory.ViewMvcFactory
import com.android.lvicto.conjugation.event.ImportConjugationsEvent
import com.android.lvicto.conjugation.usecase.ConjugationAddUseCase
import com.android.lvicto.conjugation.usecase.ConjugationFetchUseCase
import com.android.lvicto.conjugation.usecase.ConjugationImportExportUseCase
import com.android.lvicto.conjugation.view.ConjugationViewMvc
import com.android.lvicto.conjugation.view.ConjugationViewMvcImpl
import com.android.lvicto.db.entity.Conjugation
import kotlinx.coroutines.*

class ConjugationFragment : BaseFragment(), ConjugationViewMvc.Listener, ResultEventBus.Listener {

    @field:Service
    private lateinit var mConjugationAddUseCase: ConjugationAddUseCase

    @field:Service
    private lateinit var mConjugationFetchUseCase: ConjugationFetchUseCase

    @field:Service
    private lateinit var mConjugationImportExportUseCase: ConjugationImportExportUseCase

    @field:Service
    private lateinit var dialogManager: DialogManager

    @field:Service
    private lateinit var viewMvcFactory: ViewMvcFactory

    @field:Service
    private lateinit var launchResultManager: ResultLauncherManager

    @field:Service
    private lateinit var resultEventBus: ResultEventBus

    @field:Service
    private lateinit var importPickerCodeHolder: ImportPickerCodeHolder

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var mViewViewMvcImpl: ConjugationViewMvcImpl


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        injector.inject(this)
        mViewViewMvcImpl = viewMvcFactory.getConjugationViewMvc(requireActivity() as AppCompatActivity, container) // todo fix conversion
        return mViewViewMvcImpl.getRootView()
    }

    override fun onStart() {
        super.onStart()
        mViewViewMvcImpl.registerListener(this)
        resultEventBus.registerListener(this)
        if (!mViewViewMvcImpl.isFiltering) {
            filter(null)
        } // to init the RecView
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
        resultEventBus.unregisterListener(this)
        mViewViewMvcImpl.unregisterListener(this)
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

    // region private todo: move to a delegate controller
    private fun add(conjugation: Conjugation?) {
        coroutineScope.launch {
            if (conjugation != null) {
                mViewViewMvcImpl.showProgress()
                try {
                    val resAdd = mConjugationAddUseCase.addConjugation(conjugation)
                    if (resAdd is ConjugationAddUseCase.Result.Success) {
                        val resFetch = mConjugationFetchUseCase.filter()
                        if (resFetch is ConjugationFetchUseCase.Result.Success) {
                            mViewViewMvcImpl.setConjugations(resFetch.conjugations)
                        } else {
                            dialogManager.showErrorDialog("Fail to filter") {
                                // onRetry() empty for now
                            }
                        }
                    } else {
                        dialogManager.showErrorDialog("Fail to add") {
                            // onRetry() empty for now
                        }
                    }
                } catch (e: Exception) {
                    dialogManager.showErrorDialog("Unknown error (add): ${e.message}") {
                        // onRetry() empty for now
                    }
                } finally {
                    mViewViewMvcImpl.hideProgress()
                }
            }
        }
    }

    private fun filter(conjugation: Conjugation?) {
        coroutineScope.launch {
            mViewViewMvcImpl.showProgress()
            try {
                val res = mConjugationFetchUseCase.filter(conjugation)
                if (res is ConjugationFetchUseCase.Result.Success) {
                    mViewViewMvcImpl.setConjugations(res.conjugations)
                } else {
                    dialogManager.showErrorDialog("Fail to filter") {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                dialogManager.showErrorDialog("Unknown error (filter): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewViewMvcImpl.hideProgress()
            }
        }
    }

    private fun delete(conjugation: Conjugation?) {
        conjugation?.let {
            dialogManager.showConjugationDialog(it, ::deleteDialogAction)
        }
    }

    private fun deleteDialogAction(conjugation: Conjugation) {
        coroutineScope.launch {
            mViewViewMvcImpl.showProgress()
            try {
                val resAdd = mConjugationAddUseCase.delete(listOf(conjugation))
                if (resAdd is ConjugationAddUseCase.Result.Success) {
                    val filterBy = if (mViewViewMvcImpl.isFiltering) {
                        mViewViewMvcImpl.conjugation
                    } else {
                        null
                    }
                    val resFetch = mConjugationFetchUseCase.filter(filterBy)
                    if (resFetch is ConjugationFetchUseCase.Result.Success) {
                        mViewViewMvcImpl.setConjugations(resFetch.conjugations)
                    } else {
                        dialogManager.showErrorDialog("Fail to filter") {
                            // onRetry() empty for now
                        }
                    }
                }
            } catch (e: Exception) {
                dialogManager.showErrorDialog("Unknown error (delete): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewViewMvcImpl.hideProgress()
            }
        }
    }

    private fun export() {
        coroutineScope.launch {
            try {
                mViewViewMvcImpl.showProgress()
                val conjugations = mConjugationFetchUseCase.filter()
                if (conjugations is ConjugationFetchUseCase.Result.Success) {
                    val res = mConjugationImportExportUseCase.exportConjugations(
                        conjugations.conjugations,
                        Constants.FILENAME_CONJUGATION
                    )
                    if (res is ConjugationImportExportUseCase.Result.SuccessWrite) {
                        activity?.export(
                            Constants.FILENAME_CONJUGATION,
                            "Dictionary: exporting conjugations"
                        )
                    } else {
                        dialogManager.showErrorDialog("Fail to export") {
                            // onRetry() empty for now
                        }
                    }
                } else {
                    dialogManager.showErrorDialog("Fail to filter") {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                dialogManager.showErrorDialog("Unknown error (export): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewViewMvcImpl.hideProgress()
            }
        }
    }

    private fun import() {
        launchResultManager.getLauncher(requireActivity()::class.java)?.openFilePicker(importPickerCodeHolder, RESULT_CODE_PICKFILE_CONJUGATIONS)
    }

    private fun loadImports(uri: Uri) {
        coroutineScope.launch {
            try {
                mViewViewMvcImpl.showProgress()
                val res = mConjugationImportExportUseCase.importConjugations(uri)
                if (res is ConjugationImportExportUseCase.Result.SuccessRead) {
                    Log.d("conjugation_log", "loadImports: SuccessRead")
                    if (res.conjugations != null) {
                        mConjugationAddUseCase.addConjugations(res.conjugations)
                        mViewViewMvcImpl.setConjugations(res.conjugations)
                        Log.d("conjugation_log", "loadImports: SuccessRead res.conjugations != null")
                    } else {
                        dialogManager.showErrorDialog("Fail to add conjugations.") {
                            // onRetry() empty for now
                            Log.d("conjugation_log", "loadImports: SuccessRead res.conjugations == null")
                        }
                    }
                } else {
                    dialogManager.showErrorDialog("Fail to import") {
                        Log.d("conjugation_log", "loadImports: unknown exception")
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                dialogManager.showErrorDialog("Unknown error (import): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewViewMvcImpl.hideProgress()
            }
        }
    }

    private fun detect(form: String) {
        coroutineScope.launch {
            try {
                mViewViewMvcImpl.showProgress()
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
                            dialogManager.showErrorDialog("Unable to detect form")
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
                        mViewViewMvcImpl.setConjugations(conjugations)
                        val root = form.substring(0, index + 1)
                        mViewViewMvcImpl.setFormRoot(root)
                    } else {
                        mViewViewMvcImpl.setConjugations(arrayListOf())
                        mViewViewMvcImpl.setFormRoot("") // reset result
                    }
                } else { // reset
                    val res = mConjugationFetchUseCase.filter()
                    if (res is ConjugationFetchUseCase.Result.Success) {
                        mViewViewMvcImpl.setConjugations(res.conjugations)
                    } else {
                        dialogManager.showErrorDialog(
                            "Unknown exception. Unable to detect form"
                        )
                    }
                }
            } catch (e: Exception) {
                dialogManager.showErrorDialog(
                    "Unknown exception. Unable to detect form"
                )
            } finally {
                mViewViewMvcImpl.hideProgress()
            }
        }
    }

    override fun onEventReceived(event: Any) {
        when (event) {
            is ImportConjugationsEvent -> {
                // todo fetch the conjugations and populate the list
                event.intent?.data?.let {
                    loadImports(it)
                }
            }
            is ErrorEvent -> {
                dialogManager.showErrorDialog("Unable to import conjugation")
            }
            else -> {
                Log.e("", "")
            }
        }
    }

}