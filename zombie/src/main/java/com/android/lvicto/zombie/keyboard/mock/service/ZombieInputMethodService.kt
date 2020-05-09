package com.android.lvicto.zombie.keyboard.mock.service

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View.inflate
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.keyboard.ims.KeyboardType
import com.android.lvicto.zombie.keyboard.ims.activity.SettingsActivity
import com.android.lvicto.zombie.keyboard.mock.SuggestionHelper
import com.android.lvicto.zombie.keyboard.ims.view.key.CandidatesKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.ExtraIastShiftKeyView
import com.android.lvicto.zombie.keyboard.ims.viewmodel.SuggestionViewModel
import com.android.lvicto.zombie.keyboard.mock.activity.CustomKeyboardMockActivity
import com.android.lvicto.zombie.keyboard.mock.keyboard.CustomKeyboardViewMock
import com.android.lvicto.zombie.keyboard.mock.keyboard.IastKeyboardViewMock
import com.android.lvicto.zombie.keyboard.mock.keyboard.QwertyKeyboardViewMock
import com.android.lvicto.zombie.keyboard.mock.keyboard.SanskritKeyboardViewMock
import kotlinx.android.synthetic.main.activity_custom_keyboard_view.*
import java.util.*

class ZombieInputMethodService : LifecycleOwner {

    lateinit var context: Context

    private val suggestionsViewModel = SuggestionViewModel() // todo use injection

    // region keyboard params
    private val _keyboardType: MutableLiveData<KeyboardType> = MutableLiveData(KeyboardType.IAST)
    private val keyboardType: LiveData<KeyboardType>
        get() {
            return _keyboardType
        }

    private fun updateKeyboardType(type: KeyboardType) {
        _keyboardType.postValue(type)
    }

    private val _showSuggestions: MutableLiveData<Boolean> = MutableLiveData()
    private val liveDataShowSuggestions: LiveData<Boolean>
        get() = _showSuggestions
    // endregion

    // region keyboard view
    lateinit var keyboardView: CustomKeyboardViewMock
    private lateinit var qwertyKeyboardView: QwertyKeyboardViewMock
    private lateinit var sansKeyboardView: SanskritKeyboardViewMock
    private lateinit var iastKeyboardView: IastKeyboardViewMock
    // endregion

    // region target view
    private var _targetView: EditText? = null
    var targetView: EditText?
        get() {
            return _targetView
        }
        set(value) {
            _targetView = value
            Log.d("kissue", "set targetView = $_targetView")
            when (_targetView?.imeActionLabel) {
                "sanskrit" -> {
                    updateKeyboardType(KeyboardType.SA)
                }
                "iast" -> {
                    updateKeyboardType(KeyboardType.IAST)
                }
                "english" -> {
                    updateKeyboardType(KeyboardType.QWERTY)
                }
                else -> {
                }
            }
        }
    // endregion

    fun onCreate(activity: CustomKeyboardMockActivity) {
        context = activity

        qwertyKeyboardView = inflate(context, R.layout.custom_keyboard_view_qwerty_mock, null) as QwertyKeyboardViewMock
        qwertyKeyboardView.isSuggestionsOn = showsSuggestionsQwerty
        activity.keyboardViewHolderQwerty.addView(qwertyKeyboardView)

        iastKeyboardView = inflate(context, R.layout.custom_keyboard_view_iast_mock, null) as IastKeyboardViewMock
        iastKeyboardView.isSuggestionsOn = showsSuggestionsIast
        activity.keyboardViewHolderIast.addView(iastKeyboardView)

        sansKeyboardView = inflate(context, R.layout.custom_keyboard_view_sans_mock, null) as SanskritKeyboardViewMock
        sansKeyboardView.isSuggestionsOn = showsSuggestionsSans
        activity.keyboardViewHolderSans.addView(sansKeyboardView)

        keyboardType.observe(this, Observer { type ->
            type?.let {
                keyboardView = when (it) {
                    KeyboardType.QWERTY -> qwertyKeyboardView
                    KeyboardType.IAST -> iastKeyboardView
                    KeyboardType.SA -> sansKeyboardView
                }
                activity.showKeyboard(type)
            }
        })

        liveDataShowSuggestions.observe(this, Observer {
            keyboardView.isSuggestionsOn = it
        })
    }
    // endregion

// todo add all methods to emulate the InputMethodService

    fun commitText(newText: String) {
        val selectionStart = targetView?.selectionStart ?: 0
        targetView?.text?.insert(selectionStart, newText) // insert the new text
    }

    fun deleteCurrentSelection() {
        Log.d("kissue", "deleteCurrentSelection(): targetView = $targetView")
        targetView.let {
            val selectionStart = it?.selectionStart ?: 0
            it?.post {
                it.text.apply {
                    if (selectionStart > 0) {
                        delete(selectionStart - 1, selectionStart)
                        val lastWord = getCurrentKey()
                        // update suggestions
                        updateSuggestions(lastWord)
                    }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun updateSuggestions(string: String) {
        if (string.isEmpty()) {
            removeSuggestions()
        } else {
            suggestionsViewModel.getSuggestions(string).subscribe({
                if (it.isEmpty()) {
                    removeSuggestions()
                } else {
                    keyboardView.showSuggestions(it.take(8))
                }
            }, {
                Log.d("ZombieIMS", it.message ?: "No exception")
            })
        }
    }

    fun removeSuggestions() {
        keyboardView.showSuggestions()
    }

    fun completeText(suggestion: String) {
        if (suggestion.isNotEmpty()) {
            // delete current word
            val text = targetView?.text.toString()
            val selectionStart = targetView?.selectionStart ?: 0
            val currentWordIndices = SuggestionHelper.getCurrentWordIndices(text, selectionStart)
            val newText = text.replaceRange(currentWordIndices.first, currentWordIndices.second, suggestion)
            // update the edit text
            targetView?.setText(newText, TextView.BufferType.EDITABLE)
            targetView?.setSelection(targetView?.text?.length ?: 0)
            // auto-add space
            addSpace()
        }
        // clear suggestions
        removeSuggestions()
    }

    private fun addSpace() {
        commitText(" ")
    }

    fun getCurrentKey(): String {
        val text = targetView?.text.toString()
        val selectionStart = targetView?.selectionStart ?: 0
        return SuggestionHelper.getCurrentWord(text, selectionStart)
    }

    fun switchKeyBoard() {
        updateKeyboardType(when (keyboardType.value) {
            KeyboardType.IAST -> KeyboardType.SA
            KeyboardType.SA -> KeyboardType.QWERTY
            else -> KeyboardType.IAST
        })
    }

    fun setCapsOn() {
        keyboardView.shiftableKeyViews.forEach {
            it.shiftKey(true)
        }
    }

    fun setCapsOff() {
        keyboardView.shiftableKeyViews.forEach {
            it.shiftKey(false)
        }
    }

    fun autoCapsOn() {
        // todo only if qwerty; make it a setting
    }

    fun updateCandidates(keyView: CandidatesKeyView) {
        val candidatesLabels = keyView.getCandidatesLabels()
        if (candidatesLabels.isEmpty()) {
            showDigits()
        }
        val candidatesViews = keyboardView.extraKeyViews
        var lastIndex = 0
        (candidatesLabels.indices).forEach {
            candidatesViews[it].apply {
                this.setText(candidatesLabels[it])
                if (this is ExtraIastShiftKeyView && this.isUpperCase) {
                    this.setText(candidatesLabels[it].toUpperCase(Locale.getDefault()))
                } else {
                    this.setText(candidatesLabels[it])
                }

                this.enable(true)
            }
            lastIndex++
        }
        // add place holders for the remaining keyViews
        (lastIndex until candidatesViews.size).forEach {
            candidatesViews[it].apply {
                this.setPlaceholder()
                this.enable(false)
            }
        }
    }

    fun showDigits() {
        keyboardView.apply {
            this.extraKeyViews.forEach {
                it.resetText()
                it.enable(true)
            }
        }
    }

    fun performAction() {
        // todo complete
        Toast.makeText(context, "Action", Toast.LENGTH_SHORT).show()
    }

    fun goToSettings() {
        context.startActivity(SettingsActivity.intent(context))
    }

    fun setCapsOffIfNotToggled() {
        val shiftKeyView = keyboardView.shiftKeyView
        if (shiftKeyView?.isPressedUI() == true && !shiftKeyView.isPersistent) {
            setCapsOff()
            shiftKeyView.setPressedUI(false)
        }
    }

    fun resetSymbolToggle() {
        val symbolKeyView = keyboardView.getSymbolToggleByPressed()
        // hide toggleDigits key and show back digits if (symbol not persistent)
        if (symbolKeyView?.isPersistent == false) {
            keyboardView.setSymbolTogglePressed(false, symbolKeyView)
            showDigits()
        }
    }

    companion object {
        var maxSuggestions = 10 // todo make it a setting
        var showsSuggestionsSans = true // todo idem
        var showsSuggestionsIast = false // todo idem
        var showsSuggestionsQwerty = true // todo idem
        var autoCapsOnFirstLetter = false // todo idem

    }

    // will be removed
    override fun getLifecycle(): Lifecycle {
        return (context as AppCompatActivity).lifecycle
    }
}