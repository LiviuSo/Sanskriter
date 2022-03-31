package com.android.lvicto.words.controller

import android.net.Uri
import android.util.Log
import com.android.lvicto.R
import com.android.lvicto.common.ImportPickerCode
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.Constants
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.export
import com.android.lvicto.common.openFilePicker
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.db.data.Words
import com.android.lvicto.db.entity.Word
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


    fun onStart(mWordIast: String?, mWordEn: String?) {
        // inject
        mActivity.injector.inject(this)

        // register
        mViewMvc.registerListener(this)
        eventBus.registerListener(this)
        if (!isDataLoaded) {
            coroutineScope.launch {
                initWords()
                mViewMvc.setupSearchFromOutside(mWordIast, mWordEn)
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
    override fun onFilterIASTEn(searchIast: String?, searchEn: String?) {
        coroutineScope.launch {
            filterIASTEn(searchIast, searchEn)
        }
    }

    override fun onFilterEnIAST(filterEn: String, filterIast: String) {
        coroutineScope.launch {
            filterByBoth(filterIast, filterEn)
        }
    }

    override fun onExport() {
        coroutineScope.launch {
            exportWords()
        }
    }

    override fun onReadFromFile(uri: Uri?) {
        if (uri != null) {
            coroutineScope.launch {
                readFromFiles(uri)
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
            deleteWords(wordsToRemove)
        }
    }

    override fun onImport() {
        import()
    }

    override fun onEventReceived(event: Any) {
        receiveEvent(event)
    }
    // endregion

    private suspend fun initWords() {
        mViewMvc.showProgress()
        val result = wordsFetchUseCase.fetchWords()
        if (result is WordsFetchUseCase.Result.Success) {
            if (result.words.isNullOrEmpty()) {
                // todo show empty screen
            } else { // total success
                mViewMvc.setWords(result.words)
                isDataLoaded = true
            }
        } else if (result is WordsFetchUseCase.Result.Failure) {
            mDialogManager.showErrorDialog(R.string.dialog_error_message_words_fetch)
            Log.d(WordsFragment.LOG_TAG, "failed to fetch the words: ${result.message}")
        }
        mViewMvc.hideProgress()
    }

    private suspend fun exportWords() {
        mViewMvc.showProgress()
        val result = wordsFetchUseCase.fetchWords()
        if (result is WordsFetchUseCase.Result.Failure) {
            mDialogManager.showErrorDialog(R.string.dialog_error_message_fetch_words_export)
            Log.d(WordsFragment.LOG_TAG, "Unable to fetch words ${result.message}")
        } else if (result is WordsFetchUseCase.Result.Success) {
            val words = result.words
            if (words.isNotEmpty()) {
                val filename = Constants.FILENAME_WORDS // todo add date in the filename
                writeWordsToFile(Words(words), filename)
            } else {
                mDialogManager.showErrorDialog(R.string.dialog_error_message_words_empty_list)
            }
        }
        mViewMvc.hideProgress()
    }

    private suspend fun writeWordsToFile(words: Words, filename: String) {
        wordsWriteToFileUseCase.writeWordsToFile(words, filename).let { result ->
            if (result is WordsWriteToFileUseCase.Result.Success) {
                mActivity.export(result.path)
            } else if (result is WordsWriteToFileUseCase.Result.Failure) {
                mDialogManager.showErrorDialog(R.string.dialog_error_message_fetch_words_export)
            }
        }
    }

    private suspend fun filterByBoth(filterIast: String, filterEn: String) {
        val result = wordsFilterUseCase.filter(filterIast, filterEn)
        if (result is WordsFilterUseCase.Result.Success) {
            mViewMvc.setWords(result.words)
        } else if (result is WordsFilterUseCase.Result.Failure) {
            mDialogManager.showErrorDialog(R.string.dialog_error_message_words_filter)
            Log.d(WordsFragment.LOG_TAG, "unable to filter: ${result.message}")
        }
    }

    private suspend fun readFromFiles(uri: Uri) {
        when (val result = wordsReadFromFileUseCase.readWords(uri)) {
            is WordsReadFromFileUseCase.Result.Success -> {
                result.words.let {
                    if (it.isEmpty()) {
                        // todo show zero state screen
                    } else {
                        wordsInsertUseCase.insertWords(it)
                        mViewMvc.setWords(it)
                    }
                }
                mViewMvc.setWords(result.words)
            }
            is WordsReadFromFileUseCase.Result.Failure -> {
                mDialogManager.showErrorDialog(R.string.dialog_error_message_read_file)
            }
        }
    }

    private suspend fun filterIASTEn(searchIAST: String?, searchEn: String?) {
        mViewMvc.showProgress()
        if (searchIAST != null) {
            val result = wordsFilterUseCase.filter(searchIAST, true)
            if (result is WordsFilterUseCase.Result.Success) {
                if (searchEn != null) {
                    filterByBoth(searchIAST, searchEn)
                } else {
                    // nothing
                }
            } else if (result is WordsFilterUseCase.Result.Failure) {
                mDialogManager.showErrorDialog(R.string.dialog_error_message_words_filter)
                Log.e(WordsFragment.LOG_TAG, "unable to filter by $searchIAST : ${result.message}")
            }
        }
        mViewMvc.hideProgress()
    }

    private suspend fun deleteWords(wordsToRemove: List<Word>) {
        val result = wordsDeleteUseCase.deleteWords(wordsToRemove)
        if (result is WordsDeleteUseCase.Result.Success) {
            mViewMvc.unselectSelectedToRemove()
            if (!mViewMvc.isSearchVisible()) {
                initWords()
            } else {
                mViewMvc.apply {
                    filterByBoth(getSearchIASTString(), getSearchEnString())
                }
            }
        } else if (result is WordsDeleteUseCase.Result.Failure) {
            mDialogManager.showErrorDialog(R.string.dialog_error_message_words_delete)
        }
    }

    private fun receiveEvent(event: Any) {
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

    private fun import() {
        resultLauncherManager.getLauncher(mActivity::class.java)
            ?.openFilePicker(importPickerCode, Constants.RESULT_CODE_PICKFILE_WORDS)
    }

    fun bindView(viewMvc: WordsViewMvc) {
        mViewMvc = viewMvc
    }

}