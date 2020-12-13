package com.lvicto.skeyboard

import android.view.MotionEvent
import android.view.View
import com.lvicto.skeyboard.KeyboardApplication.Companion.keyboardApplication
import com.lvicto.skeyboard.service.SanskritKeyboardIms
import com.lvicto.skeyboard.view.key.BaseKeyView
import com.lvicto.skeyboard.view.key.CandidatesKeyView
import com.lvicto.skeyboard.view.key.ToggleKeyView
import com.lvicto.skeyboard.view.key.TypableKeyView
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

object KeyListeners {

    // todo use injection
    lateinit var ims: SanskritKeyboardIms

    fun getKeySuggestionClickListener() = View.OnClickListener { v ->
        val keyView = v as BaseKeyView
        keyView.setPressedUI(true)

        ims = Injector.getInstance(keyboardApplication).ims
        // add suggestion to text
        val suggestion = (keyView as TypableKeyView).getKeyLabel()
        ims.completeText(suggestion)

        keyView.setPressedUI(false)
    }

    fun getKeyToggleSystemBar() = object : BaseTouchListener() {
        override fun doOnActionDown() {
            ims = Injector.getInstance(keyboardApplication).ims
            ims.removeSuggestions()
        }
    }

    fun getKeySettingsTouchListener() = object : BaseTouchListener() {

        override fun doOnActionDown() {
            ims = Injector.getInstance(keyboardApplication).ims
            // start settings activity
            ims.goToSettings()
        }
    }

    fun getKeyActionTouchListener() = object : BaseTouchListener() {

        override fun doOnActionDown() {
            keyView.setPressedUI(true)
            // vibrate on press (short)
            vibrate()
            ims = Injector.getInstance(keyboardApplication).ims
            // remove suggestions
            ims.removeSuggestions()
            // perform action
            ims.performAction()
            // set first letter caps if qwerty and (todo) setting is on
            ims.setAutoCap()
        }
    }

    fun getKeySpaceTouchListener() = object : BaseTouchListener() {

        override fun doOnLongTap() {
            keyView.post {
                keyView.setPressedUI(false)
            }
            // switch keyboard
            ims = Injector.getInstance(keyboardApplication).ims
            ims.switchKeyBoard()
        }

        override fun doOnActionDown() {
            // set pressed
            keyView.setPressedUI(true)
        }

        override fun doOnNormalTapOnly() {
            ims = Injector.getInstance(keyboardApplication).ims
            // type a space
            ims.commitText(" ")
        }
    }

    fun getKeyShiftTouchListener() = object : BaseTouchListener() {

        override fun doOnLongTap() {
            (keyView as ToggleKeyView).apply {
                if (!this.isPressedUI()) {
                    ims = Injector.getInstance(keyboardApplication).ims
                    ims.setCapsOn()
                }
                this.post {
                    setTogglePersistent(true) // update background
                }
            }
        }

        override fun doOnNormalTapOnly() {
            (keyView as ToggleKeyView).apply {
                ims = Injector.getInstance(keyboardApplication).ims
                if (!(this.isPressedUI() || this.isPersistent)) {
                    this.setPressedUI(true)
                    ims.setCapsOn()
                } else {
                    this.setPressedUI(false)
                    ims.setCapsOff()

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

    fun getKeyDeleteTouchListener() = object : BaseTouchListener() {

        init {
            runnable = {
                val actionTime = AtomicLong()
                var isTimeToRepeat = true
                while (!actionUpFlag.get()) {
                    if (isTimeToRepeat) {
                        // vibrate short
                        vibrate()
                        ims = Injector.getInstance(keyboardApplication).ims
                        // delete selection and set the cursor after previous selection
                        ims.deleteCurrentSelection()
                        // update suggestions
                        ims.updateSuggestions()
                        // toggle first letter if necessary
                        ims.setAutoCap()
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

    fun getKeyExtraTouchListener() = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // set background pressed
            keyView.setPressedUI(true)
            ims = Injector.getInstance(keyboardApplication).ims
            // type char
            val text = keyView.text.toString()
            ims.commitText(text)

            // update suggestions
            ims.updateSuggestions()
            // reset symbols key
            ims.resetSymbolToggle()
        }
    }

    fun getKeyTypableTouchListener() = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // set pressed backgrd
            keyView.setPressedUI(true)
            // vibrate short
            vibrate()
            // show preview
            (keyView as TypableKeyView).showPreview()

            ims = Injector.getInstance(keyboardApplication).ims
            // show extras
            ims.updateCandidates(keyView as CandidatesKeyView)
            // show digits and reset any toggle
            val defaultSymbolKey = ims.keyboardView.getSymbolToggleByCandidates((keyView as TypableKeyView).candidatesResId)
            ims.keyboardView.setSymbolTogglePressed(true, defaultSymbolKey)
        }

        override fun doOnActionUp() {
            super.doOnActionUp()

            // hide preview
            (keyView as TypableKeyView).hidePreview()
        }

        override fun doOnNormalTapOnly() {
            // type the char
            ims.commitText(keyView.text.toString())

            // if caps is on but not toggled, set caps off
            ims.setCapsOffIfNotToggled()
            // update suggestions
            ims.updateSuggestions()
        }
    }

    fun getKeySymbolTouchListener() = object : BaseTouchListener() {

        override fun doOnLongTap() {
            (keyView as ToggleKeyView).apply {
                if (!this.isPressedUI()) {
                    ims.updateCandidates(this)
                }
                this.post {
                    ims.keyboardView.setSymbolTogglePressed(true, this)
                    setTogglePersistent(true) // update background
                }
            }
        }

        override fun doOnNormalTapOnly() {

            ims = Injector.getInstance(keyboardApplication).ims
            (keyView as ToggleKeyView).apply {
                if (!(this.isPressedUI() || this.isPersistent)) { // its symbols are not shown
                    ims.keyboardView.setSymbolTogglePressed(true, this)
                    ims.updateCandidates(this)
                    // show toggleDigits
                } else {
                    ims.keyboardView.setSymbolTogglePressed(false, this)
                    ims.showDigits()
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

    /**
     * Base class for the touch listeners
     */
    abstract class BaseTouchListener : View.OnTouchListener {

        protected lateinit var keyView: BaseKeyView
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
            const val LOG_TAG: String = "saiastkey"
        }
    }
}