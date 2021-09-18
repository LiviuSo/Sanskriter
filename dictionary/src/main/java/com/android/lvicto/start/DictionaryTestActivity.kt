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
                Log.d("import_log", "received code $code")
                if (it.resultCode == Activity.RESULT_OK) {
                    when(code) {
                        RESULT_CODE_PICKFILE_WORDS -> {
                            Log.d("import_log", " sending event ImportWordsIntentEvent")
                            eventBus.postEvent(ImportWordsIntentEvent(intent))
                        }
                        RESULT_CODE_PICKFILE_DECLENSIONS -> {
                            Log.d("import_log", " sending event ImportDeclensionsIntentEvent")
                            eventBus.postEvent(ImportDeclensionsIntentEvent(intent))
                        }
                        RESULT_CODE_PICKFILE_CONJUGATIONS -> {
                            Log.d("import_log", " sending event ImportConjugationsEvent")
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

//        val importDeclensionsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//
//            } else {
//                eventBus.postEvent(ErrorEvent("Received import declensions: unknown error"))
//            }
//        }
//        resultLauncherManager.registerLauncher(DeclensionFragment::class.java, importDeclensionsResultLauncher)

//        val importConjugationsResultManager = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if(it.resultCode == Activity.RESULT_OK) {
//                eventBus.postEvent(ImportConjugationsEvent(it.data))
//            } else {
//                eventBus.postEvent(ErrorEvent("Received import conjugations: unknown error"))
//            }
//        }
//        resultLauncherManager.registerLauncher(ConjugationFragment::class.java, importConjugationsResultManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        resultLauncherManager.unregisterLauncher(WordsFragment::class.java)
        resultLauncherManager.unregisterLauncher(DeclensionFragment::class.java)
        resultLauncherManager.unregisterLauncher(ConjugationFragment::class.java)
    }

}