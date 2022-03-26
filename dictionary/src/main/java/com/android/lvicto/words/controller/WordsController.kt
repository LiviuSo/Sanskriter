package com.android.lvicto.words.controller

import android.net.Uri
import android.util.Log
import com.android.lvicto.R
import com.android.lvicto.common.ImportPickerCodeHolder
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.Constants
import com.android.lvicto.common.dialog.new.DialogManager2
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.extention.openFilePicker
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
    private lateinit var importPickerCodeHolder: ImportPickerCodeHolder

    @field:Service
    private lateinit var wordsInsertUseCase: WordsInsertUseCase

    @field:Service
    private lateinit var mDialogManager2: DialogManager2

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
    override fun onFilterIastEn(searchIast: String?, searchEn: String?) {
        coroutineScope.launch {
            filterIastEn(searchIast, searchEn)
        }
    }

    override fun onFilterEnIast(filterEn: String, filterIast: String) {
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

    override fun onDeleteWords(
        wordsToRemove: List<Word>
    ) {
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
            mDialogManager2.showErrorDialog(R.string.info_dialog_words_fetch_error)
            Log.d(WordsFragment.LOG_TAG, "failed to fetch the words: ${result.message}")
        }
        mViewMvc.hideProgress()
    }

    private suspend fun exportWords() {
        mViewMvc.showProgress()
        val result = wordsFetchUseCase.fetchWords()
        if (result is WordsFetchUseCase.Result.Failure) {
            mDialogManager2.showErrorDialog(R.string.info_dialog_words_export_error)
            Log.d(WordsFragment.LOG_TAG, "Unable to fetch words ${result.message}")
        } else if (result is WordsFetchUseCase.Result.Success) {
            val words = result.words
            if (words.isNotEmpty()) {
                val filename = Constants.FILENAME_WORDS // todo make a constant for now
                writeWordsToFile(Words(words), filename)
            }
        }
        mViewMvc.hideProgress()
    }

    private suspend fun writeWordsToFile(words: Words, filename: String) {
        val result = wordsWriteToFileUseCase.writeWordsToFile(words, filename)
        if (result is WordsWriteToFileUseCase.Result.Success) {
            // do smth
        } else if (result is WordsWriteToFileUseCase.Result.Failure) {
            mDialogManager2.showErrorDialog(R.string.info_dialog_words_export_error)
        }
    }

    private suspend fun filterByBoth(filterIast: String, filterEn: String) {
        val result = wordsFilterUseCase.filter(filterIast, filterEn)
        if (result is WordsFilterUseCase.Result.Success) {
            mViewMvc.setWords(result.words)
        } else if (result is WordsFilterUseCase.Result.Failure) {
            mDialogManager2.showErrorDialog(R.string.info_dialog_words_filter_error)
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
                mDialogManager2.showErrorDialog(R.string.info_dialog_read_file_error)
                Log.e(WordsFragment.LOG_TAG, "Unable to read from file: ${result.message}")
            }
            else -> {
                Log.e(WordsFragment.LOG_TAG, "Unknown result $result")
            }
        }
    }

    private suspend fun filterIastEn(searchIast: String?, searchEn: String?) {
        mViewMvc.showProgress()
        if (searchIast != null) {
            val result = wordsFilterUseCase.filter(searchIast, true)
            if (result is WordsFilterUseCase.Result.Success) {
                if (searchEn != null) {
                    filterByBoth(searchIast, searchEn)
                } else {
                    // nothing
                }
            } else if (result is WordsFilterUseCase.Result.Failure) {
                mDialogManager2.showErrorDialog(R.string.info_dialog_words_filter_error)
                Log.e(WordsFragment.LOG_TAG, "unable to filter by $searchIast : ${result.message}")
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
                val filterEn = mViewMvc.getSearchEnString()
                val filterIast = mViewMvc.getSearchIastString()
                filterByBoth(filterIast, filterEn)
            }
        } else if (result is WordsDeleteUseCase.Result.Failure) {
            mDialogManager2.showErrorDialog(R.string.info_dialog_words_delete_error)
        }

    }

    private fun receiveEvent(event: Any) {
        when (event) {
            is ImportWordsIntentEvent -> {
                Log.d(WordsFragment.LOG_TAG, "onResultReceived")
                mViewMvc.onFilePicked(event.intent)
            }
            is ErrorEvent -> {
                mDialogManager2.showErrorDialog(R.string.info_dialog_words_import_error)
            }
            else -> {
                Log.d(WordsFragment.LOG_TAG, "WordsFragment: unknown event ${event::class.java.name}")
            }
        }
    }

    private fun import() {
        resultLauncherManager.getLauncher(mActivity::class.java)
            ?.openFilePicker(importPickerCodeHolder, Constants.RESULT_CODE_PICKFILE_WORDS)
    }

    fun bindView(viewMvc: WordsViewMvc) {
        mViewMvc = viewMvc
    }

}