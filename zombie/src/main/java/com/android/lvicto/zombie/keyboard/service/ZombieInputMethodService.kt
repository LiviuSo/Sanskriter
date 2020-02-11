package com.android.lvicto.zombie.keyboard.service

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.lvicto.zombie.keyboard.KeyboardType
import com.android.lvicto.zombie.keyboard.view.keyboard.CustomKeyboardView
import com.android.lvicto.zombie.keyboard.view.keyboard.QwertyKeyboardView
import com.android.lvicto.zombie.keyboard.view.keyboard.SanskritKeyboardView

class ZombieInputMethodService {

    private var _targetView: EditText? = null
    var targetView: EditText?
        get() {
            return _targetView
        }
        set(value) {
            _targetView = value
            if(_targetView?.imeActionLabel == "sanskrit") {
                updateKeyboardType(KeyboardType.SA)
            } else {
                updateKeyboardType(KeyboardType.IAST)
            }
        }

    // todo add all methods to emulate the InputMethodService

    fun commitText(text: String) {
        Log.d("ZombieIMS", "committing text $_targetView")
        _targetView?.text?.append(text)
    }

    fun deleteCurrentSelection() {
        Log.d("ZombieIMS", "committing text")
        _targetView.let {
            it?.post {
                it.text.apply {
                    if (length > 0) {
                        delete(length - 1, length)
                    }
                }
            }
        }
    }

    // region keyboard type
    private val _keyboardType: MutableLiveData<KeyboardType> = MutableLiveData(KeyboardType.IAST)

    val keyboardType: LiveData<KeyboardType>
        get() = _keyboardType

    fun updateKeyboardType(type: KeyboardType) {
        _keyboardType.postValue(type)
    }
    // endregion

    // region keyboard view
    private lateinit var _keyboardViewIast: QwertyKeyboardView
    private lateinit var _keyboardViewSans: SanskritKeyboardView

    fun addKeyboardViewIast(kv: QwertyKeyboardView) {
        _keyboardViewIast = kv
    }

    fun addKeyboardViewSans(kv: SanskritKeyboardView) {
        _keyboardViewSans = kv
    }

    val keyboardView: CustomKeyboardView
        get() {
            return if (keyboardType.value == KeyboardType.IAST) {
                _keyboardViewIast
            } else {
                _keyboardViewSans
            }
        }
    // endregion
}