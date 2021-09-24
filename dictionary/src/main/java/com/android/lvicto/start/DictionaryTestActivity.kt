package com.android.lvicto.start

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.android.lvicto.R
import com.android.lvicto.common.ImportPickerCodeHolder
import com.android.lvicto.common.activities.BaseActivity
import com.android.lvicto.common.constants.Constants.RESULT_CODE_PICKFILE_CONJUGATIONS
import com.android.lvicto.common.constants.Constants.RESULT_CODE_PICKFILE_DECLENSIONS
import com.android.lvicto.common.constants.Constants.RESULT_CODE_PICKFILE_WORDS
import com.android.lvicto.common.eventbus.ResultEventBus
import com.android.lvicto.common.eventbus.event.ErrorEvent
import com.android.lvicto.common.resultlauncher.ResultLauncherManager
import com.android.lvicto.conjugation.event.ImportConjugationsEvent
import com.android.lvicto.conjugation.fragment.ConjugationFragment
import com.android.lvicto.declension.event.ImportDeclensionsIntentEvent
import com.android.lvicto.declension.fragment.DeclensionFragment
import com.android.lvicto.dependencyinjection.Service
import com.android.lvicto.words.event.ImportWordsIntentEvent
import com.android.lvicto.words.fragments.WordsFragment

class DictionaryTestActivity : BaseActivity() {

    @field:Service
    private lateinit var resultLauncherManager: ResultLauncherManager

    @field:Service
    private lateinit var eventBus: ResultEventBus

    @field:Service
    private lateinit var importPickerCodeHolder: ImportPickerCodeHolder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_dictionary)

        injector.inject(this)

        // todo create a factory
        val importWordsResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val intent = it.data
                val code = importPickerCodeHolder.code
                if (it.resultCode == Activity.RESULT_OK) {
                    when (code) {
                        RESULT_CODE_PICKFILE_WORDS -> {
                            eventBus.postEvent(ImportWordsIntentEvent(intent))
                        }
                        RESULT_CODE_PICKFILE_DECLENSIONS -> {
                            eventBus.postEvent(ImportDeclensionsIntentEvent(intent))
                        }
                        RESULT_CODE_PICKFILE_CONJUGATIONS -> {
                            eventBus.postEvent(ImportConjugationsEvent(intent))
                        }
                        else -> {
                            Log.d("import_log", "unknown code: $code")
                        }
                    }
                } else {
                    eventBus.postEvent(ErrorEvent("Received import words: unknown error"))
                }
            }
        resultLauncherManager.registerLauncher(this::class.java, importWordsResultLauncher)
    }

    override fun onDestroy() {
        super.onDestroy()
        resultLauncherManager.unregisterLauncher(WordsFragment::class.java)
        resultLauncherManager.unregisterLauncher(DeclensionFragment::class.java)
        resultLauncherManager.unregisterLauncher(ConjugationFragment::class.java)
    }

}