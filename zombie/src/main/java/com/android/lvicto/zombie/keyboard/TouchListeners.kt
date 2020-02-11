package com.android.lvicto.zombie.keyboard

import android.view.MotionEvent
import android.view.View
import com.android.lvicto.zombie.keyboard.view.key.BaseKeyView
import com.android.lvicto.zombie.keyboard.view.key.CandidatesKeyView
import com.android.lvicto.zombie.keyboard.view.key.ToggleKeyView
import com.android.lvicto.zombie.keyboard.view.key.TypableKeyView
import com.android.lvicto.zombie.keyboard.view.keyboard.CustomKeyboardView
import com.android.lvicto.zombie.keyboard.view.keyboard.QwertyKeyboardView
import com.android.lvicto.zombie.keyboard.viewmodel.QwertyKeyboardViewModel
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

// todo use injection
object TouchListeners {

    val keySettingsTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // start settings activity
            keyboardView.keyboardViewModel.goToSettings()
        }
    }

    val keyActionTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            keyView.setPressedUI(true)
            // vibrate on press (short)
            vibrate()
            val keyboardViewModel = keyboardView.keyboardViewModel
            // remove suggestions
            keyboardViewModel.removeSuggestions()
            // perform action
            keyboardViewModel.performAction()
            // set first letter caps if qwerty and (todo) setting is on
            if (keyboardViewModel is QwertyKeyboardViewModel) {
                keyboardViewModel.autoCapsOn()
            }
        }
    }

    val keySuggestionTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            keyView.setPressedUI(true)
            // add suggestion to text
            val suggestion = (keyView as TypableKeyView).getKeyLabel() // todo complete
            keyboardView.keyboardViewModel.completeText(suggestion)
        }
    }

    val keyToggleDigitsTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // set pressed
            keyView.setPressedUI(true)
            // vibrate
            vibrate()
            keyboardView.keyboardViewModel.let {
                // reset long press keys and show digits
                it.resetToggled()
                // show the digits
                it.showDigits()
            }
        }
    }

    val keySpaceTouchListener = object : BaseTouchListener() {

        override fun doOnLongTap() {
            // switch keyboard
            keyboardView.keyboardViewModel.switchKeyBoard()
        }

        override fun doOnActionDown() {
            // set pressed
            keyView.setPressedUI(true)
        }

        override fun doOnNormalTapOnly() {
            // type a space
            keyboardView.keyboardViewModel.commitText(" ")
        }
    }

    val keyShiftTouchListener = object : BaseTouchListener() {

        override fun doOnLongTap() {
            (keyView as ToggleKeyView).apply {
                if (!this.isPressedUI()) {
                    (keyboardView.keyboardViewModel as QwertyKeyboardViewModel).setCapsOn()
                }
                this.setTogglePersistent(true) // update background
            }
        }

        override fun doOnNormalTapOnly() {
            (keyView as ToggleKeyView).apply {
                if (!(this.isPressedUI() || this.isPersistent)) {
                    this.setPressedUI(true)
                    (keyboardView.keyboardViewModel as QwertyKeyboardViewModel).setCapsOn()
                } else {
                    this.setPressedUI(false)
                    (keyboardView.keyboardViewModel as QwertyKeyboardViewModel).setCapsOff()

                    if (this.isPersistent) {
                        this.setTogglePersistent(false)
                    }
                }
            }
        }

        override fun doOnActionUp() {
            // nothing
        }
    }

    val keyDeleteTouchListener = object : BaseTouchListener() {

        init {
            runnable = {
                val actionTime = AtomicLong()
                var isTimeToRepeat = true
                while (!actionUpFlag.get()) {
                    if (isTimeToRepeat) {
                        // vibrate short
                        vibrate()
                        val keyboardViewModel = keyboardView.keyboardViewModel
                        // delete selection and set the cursor after previous selection
                        keyboardViewModel.deleteCurrentSelection()
                        // update suggestions
                        keyboardViewModel.updateSuggestions()
                        // toggle first letter if necessary
                        if (keyboardViewModel is QwertyKeyboardViewModel) {
                            keyboardViewModel.autoCapsOn()
                        }
                        actionTime.set(System.currentTimeMillis()) // reset to detect new repetition
                    }
                    isTimeToRepeat = System.currentTimeMillis() - actionTime.get() > DELAY_AUTO_REPEAT
                }
            }
        }

        override fun doOnActionDown() {
            // set pressed
            keyView.setPressedUI(true)
            actionUpFlag.set(false)
        }

        override fun doOnActionUp() {
            super.doOnActionUp()
            actionUpFlag.set(true)
        }
    }

    val keyExtraTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            val keyboardViewModel = keyboardView.keyboardViewModel
            // set background pressed
            keyView.setPressedUI(true)
            // type char
            keyboardViewModel.commitText(keyView.text.toString())
            // update suggestions
            keyboardViewModel.updateSuggestions()

            (keyView as CandidatesKeyView).let { kv ->
                // reset symbols key
                keyboardView.symbolKeyView.apply {
                    // hide toggleDigits key and show back digits if (symbol not persistent)
                    if (!this.isPersistent) {
                        this.setTogglePersistent(false)
                        this.setPressedUI(false)
                        keyboardViewModel.showDigits()
                    }
                }
            }
        }
    }

    val keySansTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            //  set pressed background
            keyView.setPressedUI(true)
            // vibrate short
            vibrate()
            // show preview
            (keyView as TypableKeyView).showPreview()

            keyboardView.keyboardViewModel.apply {
                // reset any toggle
                resetToggled()
                // show extras
                updateCandidates(keyView as CandidatesKeyView)
                // type char
                commitText((keyView as TypableKeyView).getKeyLabel())
            }
        }

        override fun doOnActionUp() {
            super.doOnActionUp()
            // hide preview
            (keyView as TypableKeyView).hidePreview()
        }
    }

    val keyQwertyTouchListener = object : BaseTouchListener() {

        override fun doOnLongTap() {
            // vibrate long
            vibrate(true)
            // set extras sticky
            (keyboardView.keyboardViewModel as QwertyKeyboardViewModel).updateCandidates(keyView as CandidatesKeyView)
        }

        override fun doOnActionDown() {
            // set pressed backgrd
            keyView.setPressedUI(true)
            // vibrate short
            vibrate()
            // show preview
            (keyView as TypableKeyView).showPreview()
            ((keyboardView.keyboardViewModel as QwertyKeyboardViewModel)).apply {
                // show extras
                updateCandidates(keyView as CandidatesKeyView)
                // show digits and reset any toggle
                resetToggled()
            }
        }

        override fun doOnActionUp() {
            super.doOnActionUp()

            // hide preview
            (keyView as TypableKeyView).hidePreview()
        }

        override fun doOnNormalTapOnly() {
            // type the char
            ((keyboardView.keyboardViewModel as QwertyKeyboardViewModel)).apply {
                commitText(keyView.text.toString())
                // if caps is on but not toggled, set caps off
                val shiftKeyView = (keyboardView as QwertyKeyboardView).shiftKeyView
                if (shiftKeyView.isPressedUI() && !shiftKeyView.isPersistent) {
                    setCapsOff()
                    shiftKeyView.setPressedUI(false)
                }
                // update suggestions
                updateSuggestions()
            }
        }
    }

    val keySymbolTouchListener = object : BaseTouchListener() {

        override fun doOnLongTap() {
            keyboardView.keyboardViewModel.apply {
                (keyView as ToggleKeyView).apply {
                    if (!this.isPressedUI()) {
                        updateCandidates(this)
                    }
                    this.setTogglePersistent(true) // update background
                    keyboardView.toggleDigitsKeyView.show(true)
                }
            }
        }

        override fun doOnNormalTapOnly() {
            keyboardView.keyboardViewModel.apply {

                (keyView as ToggleKeyView).apply {
                    if (!(this.isPressedUI() || this.isPersistent)) { // its symbols are not shown
                        this.setPressedUI(true)
                        updateCandidates(this)
                        // show toggleDigits
                        keyboardView.toggleDigitsKeyView.show(true)
                    } else {
                        this.setPressedUI(false)
                        showDigits()
                        if (this.isPersistent) {
                            this.setTogglePersistent(false)
                        }
                    }
                }
            }
        }

        override fun doOnActionUp() {
            // nothing
        }
    }

    /**
     * Base class for the touch listeners
     */
    abstract class BaseTouchListener : View.OnTouchListener {

        protected lateinit var keyView: BaseKeyView
        protected lateinit var keyboardView: CustomKeyboardView

        protected var actionUpFlag = AtomicBoolean(false)

        private var isRunningFlag = AtomicBoolean(false)
        private var longTap = AtomicBoolean(false)

        protected var runnable = {
            if (!isRunningFlag.get()) { // run one at a time
                isRunningFlag.set(true)
                // reset the flags
                longTap.set(false)
                actionUpFlag.set(false)

                val actionTime = AtomicLong(System.currentTimeMillis())
                while (!actionUpFlag.get()) {
                    if (System.currentTimeMillis() - actionTime.get() > LONG_PRESS_TIME) {
                        longTap.set(true)
                        actionUpFlag.set(true) // once long tap reached, end the thread
                        doOnLongTap()
                        isRunningFlag.set(false) // new thread may start
                    }
                }
            }
        }

        override fun onTouch(view: View, event: MotionEvent): Boolean = when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // fetch the key view
                keyView = view as BaseKeyView
                // inject keyboard view here
                Injector.getInstance(keyView.context).let {
                    keyboardView = it.ims.keyboardView
                }
                doOnActionDown()
                Thread(runnable).start() // start thread to catch long taps
                true
            }
            MotionEvent.ACTION_UP -> {
                actionUpFlag.set(true) // stop the thread
                isRunningFlag.set(false) // reset the thread

                if (!longTap.get()) {
                    doOnNormalTapOnly()
                }
                doOnActionUp()
                view.performClick()
                true
            }
            else -> {
                false
            }
        }

        protected fun vibrate(long: Boolean = false) {
            // todo
        }

        @Override
        protected open fun doOnLongTap() {
        }

        @Override
        protected open fun doOnActionDown() {
        }

        @Override
        protected open fun doOnActionUp() {
            keyView.setPressedUI(false)
        }

        @Override
        protected open fun doOnNormalTapOnly() {
        }

        companion object {
            const val DELAY_AUTO_REPEAT: Long = 130
            const val LONG_PRESS_TIME = 400L
            const val LOG = "BaseTouchListener"
        }
    }
}