package com.android.lvicto.sanskritkeyboard.keyboard.initializer

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.lvicto.sanskritkeyboard.*
import com.android.lvicto.sanskritkeyboard.CustomKeyboard2.Companion.LOG_TAG
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType

abstract class KbLayoutInitializer(val context: Context) {

    protected abstract fun initExtraCodes()
    protected abstract fun getView(): View
    protected abstract fun getKeyClickListener(extra: Boolean = false, text: String = ""): View.OnClickListener
    protected abstract fun getKeyLongClickListener(extra: Boolean = false, text: String = ""): View.OnLongClickListener

    protected val extraKeys: ArrayList<Button> = arrayListOf()
    protected var extraKeysCodesMap = hashMapOf<Int, List<Int>>()

    lateinit var ic: InputConnection
    lateinit var currentInputEditorInfo: EditorInfo

    private val spaceClickListener: View.OnClickListener = View.OnClickListener {
        val output = " "
        ic.commitText(output, output.length)
        disableAllExtraKeys()
        val key = (it as TextView)
        if(key.text == "Qwerty") { // todo improve
            key.text = "Sanskrit"
        } else {
            key.text = "Qwerty"
        }
    }

    protected val settingsKeyClickListener = View.OnClickListener {
        context.startActivity(SettingsActivity.intent(context))
    }

    lateinit var keyboardSwitch: KeyboardSwitch

    private val deleteOnClickListener: View.OnClickListener = View.OnClickListener {
        Log.d(LOG_TAG, "deleteOnClickListener: $ic")
        ic.deleteSurroundingText(1, 0)
        disableAllExtraKeys()
    }

    private val actionOnClickListener: View.OnClickListener = View.OnClickListener {
        val imeOptions = currentInputEditorInfo.imeOptions
        when {  // todo fix
            imeOptions == 0 -> {
                Log.d(LOG_TAG, " EditorInfo.IME_ACTION_UNSPECIFIED")
            }
            EditorInfo.IME_ACTION_PREVIOUS and imeOptions == EditorInfo.IME_ACTION_PREVIOUS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(LOG_TAG, "EditorInfo.IME_ACTION_PREVIOUS")
                } else {
                    Log.d(LOG_TAG, "EditorInfo.IME_ACTION_PREVIOUS unavailable")
                }
            }
            EditorInfo.IME_ACTION_DONE and imeOptions == EditorInfo.IME_ACTION_DONE -> {
                Log.d(LOG_TAG, "EditorInfo.IME_ACTION_DONE")
            }
            EditorInfo.IME_ACTION_NEXT and imeOptions == EditorInfo.IME_ACTION_NEXT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(LOG_TAG, "EditorInfo.IME_ACTION_NEXT")
                } else {
                    Log.d(LOG_TAG, "EditorInfo.IME_ACTION_NEXT unavailable")
                }
            }
            EditorInfo.IME_ACTION_SEND and imeOptions == EditorInfo.IME_ACTION_SEND -> {
                Log.d(LOG_TAG, "EditorInfo.IME_ACTION_SEND")
            }
            EditorInfo.IME_ACTION_SEARCH and imeOptions == EditorInfo.IME_ACTION_SEARCH -> {
                Log.d(LOG_TAG, "EditorInfo.IME_ACTION_SEARCH")
            }
            EditorInfo.IME_ACTION_GO and imeOptions == EditorInfo.IME_ACTION_GO -> {
                Log.d(LOG_TAG, "EditorInfo.IME_ACTION_GO")
            }
            EditorInfo.IME_ACTION_NONE and imeOptions == EditorInfo.IME_ACTION_NONE -> {
                Log.d(LOG_TAG, "EditorInfo.IME_ACTION_NONE")
            }
            else -> {
                Log.d(LOG_TAG, "EditorInfo.UNKNOWN")
            }
        }
    }


    fun initKeyboard(): View? = getView().apply {
        initExtraCodes()
        bindKeys(this)
    }

    protected open fun bindKeys(view: View) {
        (view button R.id.keySpace).apply {
            setOnClickListener(spaceClickListener)
            setOnLongClickListener {
                keyboardSwitch.switchKeyboard()
                true
            }
        }
        view.findViewById<Button>(R.id.keyDel).apply {
            setOnClickListener(deleteOnClickListener)
        }
        (view button R.id.keyAction).apply {
            setOnClickListener(actionOnClickListener)
        }
        (view button R.id.keySettings).apply {
            setOnClickListener(settingsKeyClickListener)
        }
    }

    protected fun showExtraKeys(code: Int) {
        val relatedChars = getRelatedCharsRes(code)
        if (relatedChars != null) {
            Log.d(LOG_TAG, "relatedChars.size: ${relatedChars.size}")
            (0 until relatedChars.size).forEach { index ->
                Log.d(LOG_TAG, "relatedChars[$index]: ${relatedChars[index]}")
                extraKeys[index].text = "${relatedChars[index].toChar()}"
                extraKeys[index].isEnabled = true
            }
        }
    }

    protected fun disableAllExtraKeys() {
        extraKeys.forEach {
            it.text = context.resources.getString(R.string.key_label_placeholder)
            it.isEnabled = false
        }
    }

    protected fun initExtraKeys(view: View) {
        extraKeys.addAll(arrayListOf(
                view button R.id.keyLetterExtra1
                , view button R.id.keyLetterExtra2
                , view button R.id.keyLetterExtra3
                , view button R.id.keyLetterExtra4
                , view button R.id.keyLetterExtra5
                , view button R.id.keyLetterExtra6
                , view button R.id.keyLetterExtra7
                , view button R.id.keyLetterExtra8
                , view button R.id.keyLetterExtra9
                , view button R.id.keyLetterExtra10
                , view button R.id.keyLetterExtra11
                , view button R.id.keyLetterExtra12
                , view button R.id.keyLetterExtra13
                , view button R.id.keyLetterExtra14
                , view button R.id.keyLetterExtra15
                , view button R.id.keyLetterExtra16
                , view button R.id.keyLetterExtra17
                , view button R.id.keyLetterExtra18)
        )
        extraKeys.forEach {
            it.isEnabled = false
            it.setOnClickListener(getKeyClickListener(true))
        }
    }

    private fun getRelatedCharsRes(code: Int): ArrayList<Int>? =
            if (extraKeysCodesMap.containsKey(code)) {
                extraKeysCodesMap[code] as ArrayList<Int>
            } else {
                null
            }

    companion object {

        fun getLayoutInitializer(context: Context, config: KeyboardConfig): KbLayoutInitializer? {

            val orientation = config.orientation
            val tablet = config.isTablet
            val keyboardType = config.type

            var kbLayoutInitializer: KbLayoutInitializer? = null
            when {
                (tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.QWERTY) -> {
                    kbLayoutInitializer = KbLayoutInitializerTabletPortraitQwerty(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.QWERTY) -> {
                    kbLayoutInitializer = KbLayoutInitializerPhonePortraitQwerty(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.QWERTY) -> {
                    kbLayoutInitializer = KbLayoutInitializerTabletLanscapeQwerty(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.QWERTY) -> {
                    kbLayoutInitializer = KbLayoutInitializerPhoneLandscapeQwertyPhonePortraitQwerty(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.SA) -> {
                    kbLayoutInitializer = KbLayoutInitializerTabletPortraitSa(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.SA) -> {
                    kbLayoutInitializer = KbLayoutInitializerPhonePortraitSa(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.SA) -> {
                    kbLayoutInitializer = KbLayoutInitializerTabletLandscapeSa(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.SA) -> {
                    kbLayoutInitializer = KbLayoutInitializerPhoneLandscapeSaPhonePortraitSa(context)
                }
                else -> {
                }
            }
            return kbLayoutInitializer
        }
    }
}