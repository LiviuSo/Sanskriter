package com.android.lvicto.words.controller

import android.net.Uri
import android.util.Log
import com.android.lvicto.R
import com.android.lvicto.common.*
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.event.ImportWordsIntentEvent
import com.android.lvicto.words.fragments.WordsFragment
import com.android.lvicto.words.usecase.*
import com.android.lvicto.words.view.WordsViewMvc
import kotlinx.coroutines.*

class WordsController(private val mActivity: BaseActivity) : WordsViewMvc.WordsViewListener, ResultEventBus.Listener {

    private lateinit var mViewMvc: WordsViewMvc
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

    fun bindView(viewMvc: WordsViewMvc) {
        mViewMvc = viewMvc
    }

    fun onStart(wordIAST: String?, wordEn: String?) {
        // inject
        mActivity.injector.inject(this)

        // register
        mViewMvc.registerListener(this)
        eventBus.registerListener(this)

        // load initial data
        if (!isDataLoaded) {
            coroutineScope.launch {
                onInitWords()
                mViewMvc.setupSearchFromOutside(wordIAST, wordEn)
            }
        }
    }

    fun onStop() {
        // unregister
        mViewMvc.unregisterListener(this)
        eventBus.unregisterListener(this)
        coroutineScope.coroutineContext.cancelChildren()
    }

    // region listener
    override fun onFilterIASTEn(searchIAST: String?, searchEn: String?) {
        coroutineScope.launch {
            if (searchIAST != null) {
                handleResult(
                    getResult = { wordsFilterUseCase.filter(searchIAST, true) },
                    isSuccess = { it is WordsFilterUseCase.Result.Success },
                    isFailure = { it is WordsFilterUseCase.Result.Failure },
                    onSuccess = { if (searchEn != null) { filterByBoth(searchIAST, searchEn) } else {/* nothing */ } },
                    onFailure = {
                        mDialogManager.showErrorDialog(R.string.dialog_error_message_words_filter)
                        Log.e(WordsFragment.LOG_TAG, "unable to filter by $searchIAST : ${(it as WordsFetchUseCase.Result.Failure).message}")
                    }
                )
            }
        }
    }

    override fun onFilterEnIAST(filterEn: String, filterIast: String) {
        coroutineScope.launch {
            filterByBoth(filterIast, filterEn)
        }
    }

    override fun onExport() {
        coroutineScope.launch {
            handleResult(
                getResult = {
//                    wordsFetchUseCase.fetchWords()
                    wordsFetchUseCase.fetchWordsPlus()
                            },
                isSuccess = {
//                    it is WordsFetchUseCase.Result.Success
                    it is WordsFetchUseCase.Result.SuccessPlus
                            },
                isFailure = { it is WordsFetchUseCase.Result.Failure },
                onSuccess = {
//                    (it as WordsFetchUseCase.Result.Success).words.apply {
                    (it as WordsFetchUseCase.Result.SuccessPlus).words.apply {
                        if (isNotEmpty()) {
                            val filename = Constants.FILENAME_WORDS_PLUS // todo add date in the filename
//                            wordsWriteToFileUseCase.writeWordsToFile(Words(this), filename).let { result -> // todo remove when migration completed
                            wordsWriteToFileUseCase.writeWordsToFilePlus(this, filename).let { result ->
                                if (result is WordsWriteToFileUseCase.Result.Success) {
                                    mActivity.export(result.path)
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
                    Log.d(WordsFragment.LOG_TAG, "Unable to fetch words ${(it as WordsFetchUseCase.Result.Failure).message}")
                }
            )
        }
    }

    override fun onReadFromFile(uri: Uri?) {
        if (uri != null) {
            coroutineScope.launch {
                handleResult(
                    getResult = {
//                        wordsReadFromFileUseCase.readWords(uri)
                        wordsReadFromFileUseCase.readWordsPlus(uri)
                                },
                    isSuccess = {
                        it is WordsReadFromFileUseCase.Result.SuccessPlus
                                },
                    isFailure = {
                        it is WordsReadFromFileUseCase.Result.Failure
                                },
                    onSuccess = {
                        (it as WordsReadFromFileUseCase.Result.SuccessPlus).words.apply {
                            if (isEmpty()) {
                                // todo show zero state screen
                            } else {
                                wordsInsertUseCase.insertWords(this.map { wordWrapper -> wordWrapper.toWord() }) // todo remove when migration completed

                                wordsInsertUseCase.insertWordsPlus(this)

                                mViewMvc.setWords(this)
                            }
                            mViewMvc.setWords(this)
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

    override fun onDeleteWords(wordsToRemove: List<WordWrapper>) {
        coroutineScope.launch {
            handleResult(
                getResult = {
//                    wordsDeleteUseCase.deleteWords(wordsToRemove) // todo remove when migration completed
                    wordsDeleteUseCase.deleteWordsPlus(wordsToRemove)
                            },
                isSuccess = { it is WordsDeleteUseCase.Result.Success },
                isFailure = { it is WordsDeleteUseCase.Result.Failure },
                onSuccess = {
                    mViewMvc.unselectSelectedToRemove()
                    if (!mViewMvc.isSearchVisible()) {
                        initWords()
                    } else {
                        mViewMvc.apply {
                            filterByBoth(getSearchIASTString(), getSearchEnString())
                        }
                    }
                },
                onFailure = { mDialogManager.showErrorDialog(R.string.dialog_error_message_words_delete) }
            )
        }
    }

    override fun onImport() {
        resultLauncherManager.getLauncher(mActivity::class.java)?.openFilePicker(importPickerCode, Constants.RESULT_CODE_PICKFILE_WORDS)
    }

    override fun onEventReceived(event: Any) {
        when (event) {
            is ImportWordsIntentEvent -> {
                mViewMvc.onFilePicked(event.intent)
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
            onSuccess = { mViewMvc.setWords((it as WordsFilterUseCase.Result.Success).words.map {
                it.toWordWrapper()
            }) },
            onFailure = {
                mDialogManager.showErrorDialog(R.string.dialog_error_message_words_filter)
                Log.d(WordsFragment.LOG_TAG, "unable to filter: ${(it as WordsFilterUseCase.Result.Failure).message}")
            }
        )
    }

    private suspend fun initWords() {
        handleResult(getResult = {
//            wordsFetchUseCase.fetchWords()
            wordsFetchUseCase.fetchWordsPlus()
                                 },
            isSuccess = {
//                it is WordsFetchUseCase.Result.Success
                it is WordsFetchUseCase.Result.SuccessPlus
                        },
            isFailure = { it is WordsFetchUseCase.Result.Failure },
            onSuccess = {
//                (it as WordsFetchUseCase.Result.Success).apply {
                (it as WordsFetchUseCase.Result.SuccessPlus).apply {
                    if (words.isNullOrEmpty()) {
                        // todo show empty screen
                    } else { // total success
//                        mViewMvc.setWords(words)
                        mViewMvc.setWords(words)
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
        mViewMvc.showProgress()
        getResult().apply {
            if (isSuccess(this)) { onSuccess(this) }
            else if(isFailure(this)) { onFailure(this) }
        }
        mViewMvc.hideProgress()
    }

}