package com.android.lvicto.words.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.lvicto.common.ImportPickerCodeHolder
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.common.constants.Constants
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.extention.export
import com.android.lvicto.common.extention.openFilePicker
import com.android.lvicto.common.fragment.BaseFragment
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.common.view.factory.ViewMvcFactory
import com.android.lvicto.db.data.Words
import com.android.lvicto.db.entity.Word
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.event.ImportWordsIntentEvent
import com.android.lvicto.words.usecase.*
import com.android.lvicto.words.view.WordsViewMvc
import com.android.lvicto.words.view.WordsViewMvcImpl
import kotlinx.coroutines.*

/*
bug: if an item is selected then unselected and then scrolled - the item is auto-selected
 */
class WordsFragment : BaseFragment(), WordsViewMvc.WordsViewListener, ResultEventBus.Listener {

    //    private val safeVarargs: WordsFragmentArgs by navArgs()
    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var viewViewMvcImpl: WordsViewMvcImpl
    private var isDataLoaded = false

    @field:Service
    private lateinit var mActivity: BaseActivity

    @field:Service
    private lateinit var resultLauncherManager: ResultLauncherManager

    @field:Service
    private lateinit var viewMvcFactory: ViewMvcFactory

    @field:Service
    private lateinit var eventBus: ResultEventBus

    @field:Service
    private lateinit var wordsFetchUseCase: WordsFetchUseCase

    @field:Service
    private lateinit var wordsDeleteUseCase: WordsDeleteUseCase

    @field:Service
    private lateinit var dialogManager: DialogManager

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


    // region lifeycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        injector.inject(this)

        viewViewMvcImpl =
            viewMvcFactory.getWordsViewMvc(requireActivity() as BaseActivity, container)

        return viewViewMvcImpl.getRootView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            viewViewMvcImpl.onActivityResult(requestCode, data)
        }
    }

    override fun onStart() {
        super.onStart()
        viewViewMvcImpl.registerListener(this)
        eventBus.registerListener(this)
        if (!isDataLoaded) {
            fetchWords()
        }
    }

    override fun onStop() {
        super.onStop()
        viewViewMvcImpl.unregisterListener(this)
        eventBus.unregisterListener(this)
        coroutineScope.coroutineContext.cancelChildren()
    }
    // endregion

    // region listener
    override fun onFilterIastEn(searchIast: String?, searchEn: String?) {
        coroutineScope.launch {
            if (searchIast != null) {
                val result = wordsFilterUseCase.filter(searchIast, true)
                if (result is WordsFilterUseCase.Result.Success) {
                    if (searchEn != null) {
                        filterByBoth(searchIast, searchEn)
                    } else {
                        // nothing
                    }
                } else if (result is WordsFilterUseCase.Result.Failure) {
                    dialogManager.showErrorDialog("Unable to filter")
                    Log.e(LOG_TAG, "unable to filter by $searchIast : ${result.message}")
                }
            }
        }
    }

    override fun onFilterEnIast(filterEn: String, filterIast: String) {
        coroutineScope.launch {
            filterByBoth(filterIast, filterEn)
        }
    }

    override fun onExport() {
        exportWords()
    }

    override fun onReadFromFile(uri: Uri?) {
        Log.d(LOG_TAG, uri?.path.toString())
        if (uri != null) {
            coroutineScope.launch {
                when (val result = wordsReadFromFileUseCase.readWords(uri)) {
                    is WordsReadFromFileUseCase.Result.Success -> {
                        result.words.let {
                            if (it.isEmpty()) {
                                // todo show zero state screen
                                dialogManager.showErrorDialog("No words (0-state screen")
                            } else {
                                wordsInsertUseCase.insertWords(it)
                                viewViewMvcImpl.setWords(it)
                            }
                        }
                        viewViewMvcImpl.setWords(result.words)
                    }
                    is WordsReadFromFileUseCase.Result.Failure -> {
                        dialogManager.showErrorDialog("Unable to read from file")
                        Log.e(LOG_TAG, "Unable to read from file: ${result.message}")
                    }
                    else -> {
                        Log.e(LOG_TAG, "Unknown result $result")
                    }
                }
            }
        } else {
            Log.d(LOG_TAG, "notifyReadFromFile(): uri is null")
        }
    }

    override fun onInitWords() {
        fetchWords()
    }

    override fun onDeleteWords(
        wordsToRemove: List<Word>
    ) {
        coroutineScope.launch {
            val result = wordsDeleteUseCase.deleteWords(wordsToRemove)
            if (result is WordsDeleteUseCase.Result.Success) {
                viewViewMvcImpl.unselectSelectedToRemove()
                if (!viewViewMvcImpl.isSearchVisible()) {
                    fetchWords()
                } else {
                    val filterEn = viewViewMvcImpl.getSearchEnString()
                    val filterIast = viewViewMvcImpl.getSearchIastString()
                    onFilterEnIast(filterEn, filterIast)
                }
            } else if (result is WordsDeleteUseCase.Result.Failure) {
                dialogManager.showErrorDialog("Unable to delete words")
            }
        }
    }

    override fun onImport() {
        resultLauncherManager.getLauncher(requireActivity()::class.java)
            ?.openFilePicker(importPickerCodeHolder, Constants.RESULT_CODE_PICKFILE_WORDS)
    }

    override fun onEventReceived(event: Any) {
        when (event) {
            is ImportWordsIntentEvent -> {
                Log.d(LOG_TAG, "onResultReceived")
                viewViewMvcImpl.onFilePicked(event.intent)
            }
            is ErrorEvent -> {
                // todo show dialog
                Toast.makeText(requireContext(), "Error: ${event.message}", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Log.d(LOG_TAG, "WordsFragment: unknown event ${event::class.java.name}")
            }
        }
    }
    // endregion

    // region private
    private fun fetchWords() {
        coroutineScope.launch {
            val result = wordsFetchUseCase.fetchWords()
            if (result is WordsFetchUseCase.Result.Success) {
                if (result.words.isNullOrEmpty()) {
                    dialogManager.showErrorDialog("Empty list (instead of zero screen)") // todo 0-state screen
                } else { // total success
                    viewViewMvcImpl.setWords(result.words)
                    isDataLoaded = true
                    Log.d(LOG_TAG, "Words fetched")
                }
            } else if (result is WordsFetchUseCase.Result.Failure) {
                dialogManager.showErrorDialog("Failed to fetch words")
                Log.d(LOG_TAG, "failed to fetch the words: ${result.message}")
            }
        }
    }

    private fun exportWords() {
        coroutineScope.launch {
            val result = wordsFetchUseCase.fetchWords()
            if (result is WordsFetchUseCase.Result.Failure) {
                dialogManager.showErrorDialog("Unable to fetch words for exporting")
                Log.d(LOG_TAG, "Unable to fetch words ${result.message}")
            } else if (result is WordsFetchUseCase.Result.Success) {
                val words = result.words
                if (words.isEmpty()) {
                    dialogManager.showErrorDialog("Empty words list while exporting")
                } else {
                    val filename = Constants.FILENAME_WORDS // todo make a constant for now
                    writeWordsToFile(Words(words), filename)
                }
            }
        }
    }

    private fun writeWordsToFile(words: Words, filename: String) {
        coroutineScope.launch {
            val result = wordsWriteToFileUseCase.writeWordsToFile(words, filename)
            if (result is WordsWriteToFileUseCase.Result.Success) {
                mActivity.export(filename = filename)
            }
        }
    }

    private suspend fun filterByBoth(filterIast: String, filterEn: String) {
        val result = wordsFilterUseCase.filter(filterIast, filterEn)
        if (result is WordsFilterUseCase.Result.Success) {
            viewViewMvcImpl.setWords(result.words)
        } else if (result is WordsFilterUseCase.Result.Failure) {
            dialogManager.showErrorDialog("unable to filter")
            Log.d(LOG_TAG, "unable to filter: ${result.message}")
        }
    }
    // endregion

    companion object {
        const val LOG_TAG = "WordsFragment_log"
    }
}