package com.android.lvicto.conjugation.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.R
import com.android.lvicto.common.*
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.common.base.BaseFragment
import com.android.lvicto.common.Constants.RESULT_CODE_PICKFILE_CONJUGATIONS
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.common.factory.ViewMvcFactory
import com.android.lvicto.conjugation.event.ImportConjugationsEvent
import com.android.lvicto.conjugation.usecase.ConjugationAddUseCase
import com.android.lvicto.conjugation.usecase.ConjugationFetchUseCase
import com.android.lvicto.conjugation.usecase.ConjugationImportExportUseCase
import com.android.lvicto.conjugation.view.ConjugationViewMvc
import com.android.lvicto.conjugation.view.ConjugationViewImpl
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
    private lateinit var importPickerCode: ImportPickerCode

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var mViewMvcImpl: ConjugationViewImpl

    // todo add controller and move to a different library


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        injector.inject(this)
        mViewMvcImpl = viewMvcFactory.getConjugationView(requireActivity() as AppCompatActivity, container) // todo fix conversion
        return mViewMvcImpl.getRootView()
    }

    override fun onStart() {
        super.onStart()
        mViewMvcImpl.registerListener(this)
        resultEventBus.registerListener(this)
        if (!mViewMvcImpl.isFiltering) {
            filter(null)
        } // to init the RecView
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
        resultEventBus.unregisterListener(this)
        mViewMvcImpl.unregisterListener(this)
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
                mViewMvcImpl.showProgress()
                try {
                    val resAdd = mConjugationAddUseCase.addConjugation(conjugation)
                    if (resAdd is ConjugationAddUseCase.Result.Success) {
                        val resFetch = mConjugationFetchUseCase.filter()
                        if (resFetch is ConjugationFetchUseCase.Result.Success) {
                            mViewMvcImpl.setConjugations(resFetch.conjugations)
                        } else {
                            dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_fail_to_filter) {
                                // onRetry() empty for now
                            }
                        }
                    } else {
                        dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_fail_to_add) {
                            // onRetry() empty for now
                        }
                    }
                } catch (e: Exception) {
                    // todo add log for the exception & create resource
                    dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unknown) {
                        // onRetry() empty for now
                    }
                } finally {
                    mViewMvcImpl.hideProgress()
                }
            }
        }
    }

    private fun filter(conjugation: Conjugation?) {
        coroutineScope.launch {
            mViewMvcImpl.showProgress()
            try {
                val res = mConjugationFetchUseCase.filter(conjugation)
                if (res is ConjugationFetchUseCase.Result.Success) {
                    mViewMvcImpl.setConjugations(res.conjugations)
                } else {
                    dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_fail_to_filter) {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                // todo add log for the exception & create resource
                dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unknown) {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvcImpl.hideProgress()
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
            mViewMvcImpl.showProgress()
            try {
                val resAdd = mConjugationAddUseCase.delete(listOf(conjugation))
                if (resAdd is ConjugationAddUseCase.Result.Success) {
                    val filterBy = if (mViewMvcImpl.isFiltering) {
                        mViewMvcImpl.conjugation
                    } else {
                        null
                    }
                    val resFetch = mConjugationFetchUseCase.filter(filterBy)
                    if (resFetch is ConjugationFetchUseCase.Result.Success) {
                        mViewMvcImpl.setConjugations(resFetch.conjugations)
                    } else {
                        dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_fail_to_filter) {
                            // onRetry() empty for now
                        }
                    }
                }
            } catch (e: Exception) {
                // todo add log for the exception & create resource
                dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unknown) {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvcImpl.hideProgress()
            }
        }
    }

    private fun export() {
        coroutineScope.launch {
            try {
                mViewMvcImpl.showProgress()
                val conjugations = mConjugationFetchUseCase.filter()
                if (conjugations is ConjugationFetchUseCase.Result.Success) {
                    val res = mConjugationImportExportUseCase.exportConjugations(
                        conjugations.conjugations,
                        Constants.FILENAME_CONJUGATION
                    )
                    if (res is ConjugationImportExportUseCase.Result.SuccessWrite) {
                        requireActivity().export(
                            requireActivity().getFilePath(Constants.FILENAME_CONJUGATION),
                            "Dictionary: exporting conjugations"
                        )
                    } else {
                        dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_fail_to_export) {
                            // onRetry() empty for now
                        }
                    }
                } else {
                    dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_fail_to_filter) {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                // todo add log for the exception & create resource
                dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unknown) {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvcImpl.hideProgress()
            }
        }
    }

    private fun import() {
        launchResultManager.getLauncher(requireActivity()::class.java)?.openFilePicker(importPickerCode, RESULT_CODE_PICKFILE_CONJUGATIONS)
    }

    private fun loadImports(uri: Uri) {
        coroutineScope.launch {
            try {
                mViewMvcImpl.showProgress()
                val res = mConjugationImportExportUseCase.importConjugations(uri)
                if (res is ConjugationImportExportUseCase.Result.SuccessRead) {
                    if (res.conjugations != null) {
                        mConjugationAddUseCase.addConjugations(res.conjugations)
                        mViewMvcImpl.setConjugations(res.conjugations)
                    } else {
                        dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_fail_to_add_conjugation) {
                            // onRetry() empty for now
                        }
                    }
                } else {
                    dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_fail_to_import) {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                // todo add log for the exception & create resource
                dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unknown) {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvcImpl.hideProgress()
            }
        }
    }

    private fun detect(form: String) {
        coroutineScope.launch {
            try {
                mViewMvcImpl.showProgress()
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
                            dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unable_to_detect_form)
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
                        mViewMvcImpl.setConjugations(conjugations)
                        val root = form.substring(0, index + 1)
                        mViewMvcImpl.setFormRoot(root)
                    } else {
                        mViewMvcImpl.setConjugations(arrayListOf())
                        mViewMvcImpl.setFormRoot("") // reset result
                    }
                } else { // reset
                    val res = mConjugationFetchUseCase.filter()
                    if (res is ConjugationFetchUseCase.Result.Success) {
                        mViewMvcImpl.setConjugations(res.conjugations)
                    } else {
                        dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unknown)
                    }
                }
            } catch (e: Exception) {
                dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unknown)
            } finally {
                mViewMvcImpl.hideProgress()
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
                dialogManager.showErrorDialogWithRetry(R.string.dialog_error_message_unable_to_import_conjugation)
            }
            else -> {
                Log.e("", "")
            }
        }
    }

}