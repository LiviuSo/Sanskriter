package com.android.lvicto.declension.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.R
import com.android.lvicto.common.ImportPickerCodeHolder
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.Constants
import com.android.lvicto.common.Constants.BASE_LOG
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.extention.export
import com.android.lvicto.common.extention.openFilePicker
import com.android.lvicto.common.base.BaseFragment
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.common.view.ViewMvcFactory
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.Declensions
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.db.entity.Word
import com.android.lvicto.declension.event.ImportDeclensionsIntentEvent
import com.android.lvicto.declension.usecase.*
import com.android.lvicto.declension.view.DeclensionsViewMvcImpl
import com.android.lvicto.declension.view.interf.DeclensionsViewMvc
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.usecase.WordsFilterUseCase
import kotlinx.android.synthetic.main.fragment_declension.*
import kotlinx.coroutines.*

/*
todos:
- dialog when adding
- dialog on deleting
- spinner
- detection
 */

class DeclensionFragment : BaseFragment(), ResultEventBus.Listener, DeclensionsViewMvc.Listener {

    @field:Service
    private lateinit var wordsFilterUseCase: WordsFilterUseCase

    @field:Service
    private lateinit var resultLauncherManager: ResultLauncherManager

    @field:Service
    private lateinit var eventBus: ResultEventBus

    @field:Service
    private lateinit var importPickerCodeHolder: ImportPickerCodeHolder

    @field:Service
    private lateinit var declensionDeleteUseCase: DeclensionDeleteUseCase

    @field:Service
    private lateinit var declensionFetchUseCase: DeclensionFetchUseCase

    @field:Service
    private lateinit var declensionFilterUseCase: DeclensionFilterUseCase

    @field:Service
    private lateinit var declensionInsertUseCase: DeclensionInsertUseCase

    @field:Service
    private lateinit var declensionsReadFromFileUseCase: DeclensionsReadFromFileUseCase

    @field:Service
    private lateinit var declensionWriteToFileUseCase: DeclensionWriteToFileUseCase

    @field:Service
    private lateinit var dialogManager: DialogManager

    @field:Service
    private lateinit var mViewFactory: ViewMvcFactory

    private lateinit var mViewMvcImpl: DeclensionsViewMvcImpl


    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private fun exportDeclensions(declensions: List<Declension>) {
        coroutineScope.launch {
            val filename = Constants.FILENAME_DECLENSION // todo make a constant for now
            val result =
                declensionWriteToFileUseCase.writeDataToFile(Declensions(declensions), filename)
            if (result is DeclensionWriteToFileUseCase.Result.Success) {
                requireActivity().export(filename = filename)
                // todo show dialog when done
            } else if (result is DeclensionWriteToFileUseCase.Result.Failure) {
                result.message.let { dialogManager.showErrorDialog(it) }
            }
        }
    }

    override fun onFilter() {
        coroutineScope.launch {
            val declension: Declension = collectData()
            Log.d(LOG_TAG, "filterObserver by $declension")
            val result = declensionFilterUseCase.filter(declension)
            if (result is DeclensionFilterUseCase.Result.Success) {
                mViewMvcImpl.setDeclensions(result.declensions)
            } else if (result is DeclensionFilterUseCase.Result.Failure) {
                dialogManager.showErrorDialog(result.message)
            }
        }
    }

    override fun onDetectDeclension(declension: String) {
        coroutineScope.launch {
            Log.d(LOG_TAG, "onDetectDeclension: $declension")
            detectDeclensionObserver(declension)
        }
    }

    private fun collectData(): Declension {
        val converters = Converters()
        return Declension(
            0,
            converters.toGramaticalCase(spinnerFilterCase.selectedItem.toString()),
            converters.toGramaticalNumber(spinnerFilterNumber.selectedItem.toString()),
            converters.toGramaticalGender(spinnerFilterGender.selectedItem.toString()),
            editTextFilterParadigm.text.toString(),
            editTextFilterEnding.text.toString(),
            editTextFilterSuffix.text.toString(),
            "" // not needed
        )
    }

    private suspend fun detectDeclension(word: String): List<Declension> {
        val declensions = arrayListOf<Declension>()
        if (word.isEmpty()) { // if empty return all declensions
            val result = declensionFilterUseCase.filter(collectData())
            if (result is DeclensionFilterUseCase.Result.Success) {
                declensions.addAll(result.declensions)
                declensionInsertUseCase.insert(result.declensions)
                Log.d(LOG_TAG, "Inserting declensions ${result.declensions}")
            } else if (result is DeclensionFilterUseCase.Result.Failure) {
                Log.d(LOG_TAG, "Error fetching declensions")
            }
        } else {
            val size = word.length
            var index = size - 1
            while (index > 0) {
                val result = declensionFetchUseCase.getSuffixes(word.substring(index, size))
                if (result is DeclensionFetchUseCase.Result.SuccessSuffixes) {
                    val suffixes = result.declensions.distinct()
                    if (suffixes.isNotEmpty()) {
                        suffixes.map { suffix ->
                            when (val suffixesResult =
                                declensionFilterUseCase.filterByFullSuffix(suffix)) {
                                is DeclensionFilterUseCase.Result.Success -> {
                                    suffixesResult.declensions.map { declension ->
                                        declensions.add(declension)
                                    }
                                }
                                is DeclensionFilterUseCase.Result.Failure -> {
                                    Log.d(LOG_TAG, "Error fetching suffixes")
                                }
                                else -> {
                                    // nothing
                                }
                            }
                        }
                    }
                    index--
                }
            }
        }
        return declensions
    }

    // first detect the declension(s),
    // then searching the roots in the dic that have that declension
    private fun detectDeclensionObserver(declensionWord: String) {
        coroutineScope.launch {
            val declensions = detectDeclension(declensionWord.toString())
            mViewMvcImpl.setDeclensions(declensions)
            declensions.distinctBy { declension ->
                declension.suffix
            }.map { declension ->
                coroutineScope.launch {
                    if (declensionWord.isNotEmpty()) {
                        val sizeDeclWord = declensionWord.length
                        val root = "${
                            declensionWord.substring(0, sizeDeclWord - declension.suffix.length)
                        }${declension.paradigmEnding}"
                        Log.d(
                            LOG_TAG,
                            "filterWordsUseCase.filter($root, ${declension.paradigm}, true"
                        )
                        val result = wordsFilterUseCase.filter(root, declension.paradigm, true)
                        if (result is WordsFilterUseCase.Result.Success) {
                            onDeclensionDetected(declension, result.words)
                            Log.d(LOG_TAG, "got words ${result.words}")
                        } else if (result is WordsFilterUseCase.Result.Failure) {
                            Log.d(LOG_TAG, "Failure: ${result.message}")
                        }
                    } else {
                        tvResults.text = "No results yet."
                    }
                }
            }
        }
    }

    private fun onDeclensionDetected(declension: Declension, dicRes: List<Word>) {
        if (dicRes.isNotEmpty()) {
            tvResults.text = StringBuffer().let { sb ->
                dicRes.map {
                    sb.append("${it.wordIAST} ${declension.gCase.abbr} ${declension.gNumber.abbr} ${declension.gGender.abbr}\n")
                }.first().toString()
            }
        } else {
            tvResults.text = "No results yet (empty list)."
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        injector.inject(this)
        mViewMvcImpl = mViewFactory.getDeclensionViewMvc(requireActivity() as BaseActivity)
        return mViewMvcImpl.setUpUI(inflater, container, R.layout.fragment_declension)
    }

    override fun onStart() {
        super.onStart()

        mViewMvcImpl.registerListener(this)
        eventBus.registerListener(this)

        onFetchAll()
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregisterListener(this)
        mViewMvcImpl.unregisterListener(this)
        coroutineScope
            .coroutineContext.cancelChildren()
    }

    companion object {
        val LOG_TAG = BASE_LOG + this::class.java.simpleName
    }

    override fun onEventReceived(event: Any) {
        when (event) {
            is ImportDeclensionsIntentEvent -> {
                Log.d("Declension_log", "receiving ImportDeclensionsIntentEvent")
                event.intent?.data?.let { uri ->
                    coroutineScope.launch {
                        declensionsReadFromFileUseCase.loadDeclensionsFromFile(uri).let {
                            if (it is DeclensionsReadFromFileUseCase.Result.Success) {
                                declensionInsertUseCase.insert(it.declensions)
                                mViewMvcImpl.setDeclensions(it.declensions)
                            } else if (it is DeclensionsReadFromFileUseCase.Result.Failure) {
                                dialogManager.showErrorDialog("Unable to read from file")
                                Log.d(LOG_TAG, "Unable to read from file: ${it.message}")
                            }
                        }
                    }
                }
            }
            is ErrorEvent -> {
                Log.d("Declension_log", "error onEventReceived() declensions")
            }
            else -> {
                Log.d(LOG_TAG, "DeclensionsFragment: unknown event")
            }
        }
    }

    override fun onDeleteDeclension(declension: Declension) {
        Log.d(LOG_TAG, "delete declension: $declension")
        coroutineScope.launch {
            declensionDeleteUseCase.delete(declension).let { resultDelete ->
                if (resultDelete is DeclensionDeleteUseCase.Result.Success) {
                    declensionFetchUseCase.getAll().let { fetchResult ->
                        if (fetchResult is DeclensionFetchUseCase.Result.Success) {
                            mViewMvcImpl.setDeclensions(fetchResult.declensions)
                        } else if (fetchResult is DeclensionFetchUseCase.Result.Failure) {
                            dialogManager.showErrorDialog("Error fetching declensions")
                            Log.d(
                                LOG_TAG,
                                "Error fetching declensions : ${fetchResult.message}"
                            )
                        }
                    }
                } else if (resultDelete is DeclensionDeleteUseCase.Result.Failure) {
                    dialogManager.showErrorDialog("Error deleting declension")
                    Log.d(LOG_TAG, "Error fetching declensions : ${resultDelete.message}")
                }
            }
        }
    }

    override fun onFetchAll() {
        coroutineScope.launch {
            declensionFetchUseCase.getAll().let {
                if (it is DeclensionFetchUseCase.Result.Success) {
                    Log.d(LOG_TAG, "onFetchAll success")
                    mViewMvcImpl.setDeclensions(it.declensions)
                } else if (it is DeclensionFetchUseCase.Result.Failure) {
                    Log.d(LOG_TAG, "Error fetching declensions : ${it.message}")
                    dialogManager.showErrorDialog("")
                }
            }
        }
    }

    override fun onSaveDeclensions(declension: Declension) {
        coroutineScope.launch {
            // insert into the DB
            declensionInsertUseCase.insert(declension).let { resultInsert ->
                if (resultInsert is DeclensionInsertUseCase.Result.Success) {
                    declensionFilterUseCase.filter(collectData()).let { resultFetch ->
                        if (resultFetch is DeclensionFilterUseCase.Result.Success) {
                            mViewMvcImpl.setDeclensions(resultFetch.declensions)
                        } else if (resultFetch is DeclensionFilterUseCase.Result.Failure) {
                            dialogManager.showErrorDialog("Error fetching declensions")
                            Log.d(
                                LOG_TAG,
                                "Error filtering declensions ${resultFetch.message}"
                            )
                        }
                    }
                } else if (resultInsert is DeclensionInsertUseCase.Result.Failure) {
                    dialogManager.showErrorDialog("Error fetching declensions")
                    Log.d(
                        LOG_TAG,
                        "Error fetching declensions ${resultInsert.message}"
                    )
                }
            }
        }
    }

    override fun onButtonImport() {
        resultLauncherManager.getLauncher(requireActivity()::class.java)
            ?.openFilePicker(importPickerCodeHolder, Constants.RESULT_CODE_PICKFILE_DECLENSIONS)
    }

    override fun onButtonExport() {
        coroutineScope.launch {
            val result = declensionFetchUseCase.getAll()
            if (result is DeclensionFetchUseCase.Result.Success) {
                exportDeclensions(result.declensions)
            } else if (result is DeclensionFetchUseCase.Result.Failure) {
                dialogManager.showErrorDialog("Unable to fetch declesions")
            }
        }
    }
}