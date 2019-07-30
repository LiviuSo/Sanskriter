package com.android.lvicto.sanskritkeyboard

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.Toast

abstract class KeyboardLayoutInitializer(val context: Context) {

    protected abstract fun initExtraCodes()
    protected abstract fun getView(): View
    protected abstract fun bindKeys(view: View)
    protected abstract fun getKeyClickListener(extra: Boolean = false, text: String = ""): View.OnClickListener
    protected abstract fun getKeyLongClickListener(extra: Boolean = false, text: String = ""): View.OnLongClickListener

    lateinit var ic: InputConnection

    fun initKeyboard(): View? {
        initExtraCodes()
        val view = getView()
        bindKeys(view)
        return view
    }

    protected val extraKeys: ArrayList<Button> = arrayListOf()
    protected var extraKeysCodesMap = hashMapOf<Int, List<Int>>()

    private fun getRelatedCharsRes(code: Int): ArrayList<Int>? {
        return if (extraKeysCodesMap.containsKey(code)) {
            extraKeysCodesMap[code] as ArrayList<Int>
        } else {
            null
        }
    }

    fun showExtraKeys(code: Int) {
        val relatedChars = getRelatedCharsRes(code)
        if (relatedChars != null) {
            Log.d(CustomKeyboard2.LOG_TAG, "relatedChars.size: ${relatedChars.size}")
            (0 until relatedChars.size).forEach { index ->
                Log.d(CustomKeyboard2.LOG_TAG, "relatedChars[$index]: ${relatedChars[index]}")
                extraKeys[index].text = "${relatedChars[index].toChar()}"
                extraKeys[index].isEnabled = true
            }
        }
    }

    fun disableAllExtraKeys() {
        extraKeys.forEach {
            it.text = context.resources.getString(R.string.key_placeholder)
            it.isEnabled = false
        }
    }

    val settingsKeyClickListener = View.OnClickListener {
        Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
    }

    companion object {

        fun getLyoutInitializer(context: Context, config: KeyboardConfig): KeyboardLayoutInitializer? {

            val orientation = config.orientation
            val tablet = config.isTablet
            val keyboardType = config.type

            var keyboardLayoutInitializer: KeyboardLayoutInitializer? = null
            when {
                (tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.QWERTY)  -> {
                    keyboardLayoutInitializer = TabletPortraitQwertyKbLayoutInitializer(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.QWERTY) -> {
                    keyboardLayoutInitializer = PhonePortraitQwertyKbLayoutInitializer(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.QWERTY) -> {
                    keyboardLayoutInitializer = TabletLanscapeQwertyKbLayoutInitializer(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.QWERTY) -> {
                    keyboardLayoutInitializer = PhoneLandscapeQwertyKbLayoutInitializer(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.SA)  -> {
                    keyboardLayoutInitializer = TabletPortraitSaKbLayoutInitializer(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_PORTRAIT && keyboardType == KeyboardType.SA) -> {
                    keyboardLayoutInitializer = PhonePortraitSaKbLayoutInitializer(context)
                }
                (tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.SA) -> {
                    keyboardLayoutInitializer = TabletLanscapeSaKbLayoutInitializer(context)
                }
                (!tablet && orientation == Configuration.ORIENTATION_LANDSCAPE && keyboardType == KeyboardType.SA) -> {
                    keyboardLayoutInitializer = PhoneLandscapeSaKbLayoutInitializer(context)
                }
                else -> {
                }
            }
            return keyboardLayoutInitializer
        }
    }
}