package com.android.lvicto.conjugation

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.lvicto.R
import com.android.lvicto.common.db.entity.Conjugation
import com.android.lvicto.common.util.Constants
import com.android.lvicto.common.util.Constants.Dictionary.FILENAME_CONJUGATION
import com.android.lvicto.common.util.export
import com.android.lvicto.common.util.openFilePicker
import com.android.lvicto.conjugation.data.ConjugationAddUseCase
import com.android.lvicto.conjugation.data.ConjugationFetchUseCase
import com.android.lvicto.conjugation.data.ConjugationImportExportUseCase
import com.android.lvicto.conjugation.view.ConjugationViewMvc
import com.android.lvicto.ui.dialog.ErrorDialog
import kotlinx.coroutines.*
import java.lang.Exception

class ConjugationActivity : AppCompatActivity() {

    private lateinit var mViewMvc: ConjugationViewMvc // todo injection
    private lateinit var mConjugationAddUseCase: ConjugationAddUseCase
    private lateinit var mConjugationFetchUseCase: ConjugationFetchUseCase
    private lateinit var mConjugationImportExportUseCase: ConjugationImportExportUseCase

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mConjugationAddUseCase = ConjugationAddUseCase(this)
        mConjugationFetchUseCase = ConjugationFetchUseCase(this)
        mConjugationImportExportUseCase = ConjugationImportExportUseCase(this)

        mViewMvc = ConjugationViewMvc(this,
            this::add,
            this::delete,
            this::filter,
            this::detect,
            this::import,
            this::export
        )
        mViewMvc.init(R.layout.activity_conjugation)
        filter(null) // to init the RecView
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.Dictionary.PICKFILE_RESULT_CODE) {
            data?.data?.let {
                loadImports(it)
            }
        }
    }

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
                if(res is ConjugationFetchUseCase.Result.Success) {
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

    private fun detect(ending: String) {
        coroutineScope.launch {
            mViewMvc.showProgress()
            try {
                val res = mConjugationFetchUseCase.filterByEnding(ending)
                if(res is ConjugationFetchUseCase.Result.Success) {
                    mViewMvc.setConjugations(res.conjugations)
                } else {
                    mViewMvc.showErrorDialog("Fail to detect") {
                        // onRetry() empty for now
                    }
                }
            } catch (e: Exception) {
                mViewMvc.showErrorDialog("Unknown error (detect): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvc.hideProgress()
            }
        }
    }

    private fun delete(conjugation: Conjugation?) {
        coroutineScope.launch {
            mViewMvc.showProgress()
            try {
                if(conjugation != null) {
                    val resAdd = mConjugationAddUseCase.delete(listOf(conjugation))
                    if (resAdd is ConjugationAddUseCase.Result.Success) {
                        val resFetch = mConjugationFetchUseCase.filter()
                        if (resFetch is ConjugationFetchUseCase.Result.Success) {
                            mViewMvc.setConjugations(resFetch.conjugations)
                        } else {
                            mViewMvc.showErrorDialog("Fail to filter") {
                                // onRetry() empty for now
                            }                        }
                    } else {
                        mViewMvc.showErrorDialog("Fail to delete") {
                            // onRetry() empty for now
                        }                    }
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
                if(conjugations is ConjugationFetchUseCase.Result.Success) {
                    val res = mConjugationImportExportUseCase.exportConjugations(conjugations.conjugations, FILENAME_CONJUGATION)
                    if(res is ConjugationImportExportUseCase.Result.SuccessWrite) {
                        this@ConjugationActivity.export(FILENAME_CONJUGATION, "Dictionary: exporting conjugations")
                    } else {
                        mViewMvc.showErrorDialog("Fail to export") {
                            // onRetry() empty for now
                        }                    }
                } else {
                    mViewMvc.showErrorDialog("Fail to filter") {
                        // onRetry() empty for now
                    }                }
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
                if(res is ConjugationImportExportUseCase.Result.SuccessRead) {
                    if(res.conjugations != null) {
                        mConjugationAddUseCase.addConjugations(res.conjugations)
                        mViewMvc.setConjugations(res.conjugations)
                    } else {
                        mViewMvc.showErrorDialog("Fail to add conjugations.") {
                            // onRetry() empty for now
                        }                    }
                } else {
                    mViewMvc.showErrorDialog("Fail to import") {
                        // onRetry() empty for now
                    }                }
            } catch (e: Exception) {
                mViewMvc.showErrorDialog("Unknown error (import): ${e.message}") {
                    // onRetry() empty for now
                }
            } finally {
                mViewMvc.hideProgress()
            }
        }
    }

}