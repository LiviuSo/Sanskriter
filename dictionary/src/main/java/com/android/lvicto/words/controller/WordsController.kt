package com.android.lvicto.words.controller

import android.net.Uri
import android.util.Log
import com.android.lvicto.R
import com.android.lvicto.common.*
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.ControllerMvcImpl
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.event.ImportWordsIntentEvent
import com.android.lvicto.words.fragments.WordsFragment
import com.android.lvicto.words.usecase.*
import com.android.lvicto.words.view.WordsView
import com.android.lvicto.words.view.WordsViewImpl
import kotlinx.coroutines.*

class WordsController(private val activity: BaseActivity) : ControllerMvcImpl<WordsView>(), WordsView.Listener, ResultEventBus.Listener {

    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @field:Service
    private lateinit var resultLauncherManager: ResultLauncherManager

    @field:Service
    private lateinit var eventBus: ResultEventBus

    @field:Service
    private lateinit var wordsFetchUseCase: WordsFetchUseCase

    @field:Service
    private lateinit var wordsDeleteUseCase: WordsDeleteUseCase

    @field:Service
    private lateinit var wordsReadFromFileUseCase: WordsReadFromFileUseCase

    @field:Service
    private lateinit var wordsWriteToFileUseCase: WordsWriteToFileUseCase

    @field:Service
    private lateinit var wordsFilterUseCase: WordsFilterUseCase

    @field:Service
    private lateinit var importPickerCode: ImportPickerCode

    @field:Service
    private lateinit var wordsInsertUseCase: WordsInsertUseCase

    @field:Service
    private lateinit var mDialogManager: DialogManager

    private var isDataLoaded = false

    var wordIAST: String? = null
    var wordEn: String? = null


    override fun onStart() {
        // inject
        activity.injector.inject(this)

        // register
        view?.registerListener(this)
        eventBus.registerListener(this)

        // load initial data
        if (!isDataLoaded) {
            coroutineScope.launch {
                view?.setupSearchFromOutside(wordIAST, wordEn)
            }
        }
    }

    override fun onStop() {
        // unregister
        view?.unregisterListener(this)
        eventBus.unregisterListener(this)
        coroutineScope.coroutineContext.cancelChildren()
    }

    // region listener view
    override fun onFilterEnIAST(filterEn: String, filterIast: String) {
        coroutineScope.launch {
            filterByBoth(filterIast, filterEn)
        }
    }

    override fun onExport() {
        coroutineScope.launch {
            handleResult(
                getResult = { wordsFetchUseCase.fetchWords() },
                isSuccess = { it is WordsFetchUseCase.Result.Success },
                isFailure = { it is WordsFetchUseCase.Result.Failure },
                onSuccess = {
                    (it as WordsFetchUseCase.Result.Success).words.apply {
                        if (isNotEmpty()) {
                            val filename = Constants.FILENAME_WORDS_PLUS // todo add date in the filename
                            wordsWriteToFileUseCase.writeWordsToFile(this, filename).let { result ->
                                if (result is WordsWriteToFileUseCase.Result.Success) {
                                    activity.export(result.path)
                                } else if (result is WordsWriteToFileUseCase.Result.Failure) {
                                    mDialogManager.showErrorDialog(R.string.dialog_error_message_fetch_words_export)
                                }
                            }
                        } else {
                            mDialogManager.showErrorDialog(R.string.dialog_error_message_words_empty_list)
                        }
                    }
                },
                onFailure = {
                    mDialogManager.showErrorDialog(R.string.dialog_error_message_fetch_words_export)
                    Log.e(WordsFragment.LOG_TAG, "Unable to fetch words ${(it as WordsFetchUseCase.Result.Failure).message}")
                }
            )
        }
    }

    override fun onReadFromFile(uri: Uri?) {
        if (uri != null) {
            coroutineScope.launch {
                handleResult(
                    getResult = { wordsReadFromFileUseCase.readWords(uri) },
                    isSuccess = { it is WordsReadFromFileUseCase.Result.Success },
                    isFailure = { it is WordsReadFromFileUseCase.Result.Failure },
                    onSuccess = {
                        (it as WordsReadFromFileUseCase.Result.Success).words.apply {
                            if (isEmpty()) {
                                // todo show zero state screen
                            } else {
                                wordsInsertUseCase.insertWords(this)
                            }
                            view?.setWords(this)
                        }
                    },
                    onFailure = { mDialogManager.showErrorDialog(R.string.dialog_error_message_read_file) }
                )
            }
        } else {
            Log.d(WordsFragment.LOG_TAG, "notifyReadFromFile(): uri is null")
        }
    }

    override fun onInitWords() {
        coroutineScope.launch {
            initWords()
        }
    }

    override fun onDeleteWords(wordsToRemove: List<Word>) {
        coroutineScope.launch {
            handleResult(
                getResult = { wordsDeleteUseCase.deleteWords(wordsToRemove) },
                isSuccess = { it is WordsDeleteUseCase.Result.Success },
                isFailure = { it is WordsDeleteUseCase.Result.Failure },
                onSuccess = {
                    view?.unselectSelectedToRemove()
                    if (view?.isSearchVisible() == false) {
                        initWords()
                    } else {
                        view?.apply {
                            filterByBoth(getSearchIASTString(), getSearchEnString())
                        }
                    }
                },
                onFailure = { mDialogManager.showErrorDialog(R.string.dialog_error_message_words_delete) }
            )
        }
    }

    override fun onImport() {
        resultLauncherManager.getLauncher(activity::class.java)?.openFilePicker(importPickerCode, Constants.RESULT_CODE_PICKFILE_WORDS)
    }
    // endregion

    // region event bus
    override fun onEventReceived(event: Any) {
        when (event) {
            is ImportWordsIntentEvent -> {
                view?.onFilePicked(event.intent)
            }
            is ErrorEvent -> {
                mDialogManager.showErrorDialog(R.string.dialog_error_message_words_import)
            }
            else -> {
                Log.d(WordsFragment.LOG_TAG, "WordsFragment: unknown event ${event::class.java.name}")
            }
        }
    }
    // endregion

    private suspend fun filterByBoth(filterIAST: String, filterEn: String) {
        handleResult(
            getResult = { wordsFilterUseCase.filter(filterIAST, filterEn) },
            isSuccess = { it is WordsFilterUseCase.Result.Success },
            isFailure = { it is WordsFilterUseCase.Result.Failure },
            onSuccess = { view?.setWords((it as WordsFilterUseCase.Result.Success).words) },
            onFailure = {
                mDialogManager.showErrorDialog(R.string.dialog_error_message_words_filter)
                Log.d(WordsFragment.LOG_TAG, "unable to filter: ${(it as WordsFilterUseCase.Result.Failure).message}")
            })
    }

    private suspend fun initWords() {
        handleResult(getResult = { wordsFetchUseCase.fetchWords() },
            isSuccess = { it is WordsFetchUseCase.Result.Success },
            isFailure = { it is WordsFetchUseCase.Result.Failure },
            onSuccess = {
                (it as WordsFetchUseCase.Result.Success).apply {
                    if (words.isEmpty()) {
                        // todo show empty screen
                    } else { // total success
                        view?.setWords(words)
                        isDataLoaded = true
                    }
                }
            },
            onFailure = {
                mDialogManager.showErrorDialog(R.string.dialog_error_message_words_fetch)
                Log.d(WordsFragment.LOG_TAG, "failed to fetch the words: ${(it as WordsFetchUseCase.Result.Failure).message}")
            }
        )
    }

    private suspend fun handleResult(getResult: suspend () -> Any,
                                     isSuccess: suspend (Any) -> Boolean, isFailure: suspend (Any) -> Boolean,
                                     onSuccess: suspend (Any) -> Unit, onFailure: suspend (Any) -> Unit) {
        view?.showProgress()
        getResult().apply {
            if (isSuccess(this)) { onSuccess(this) }
            else if(isFailure(this)) { onFailure(this) }
        }
        view?.hideProgress()
    }

}