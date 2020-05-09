package com.android.lvicto.zombie.keyboard.mock

import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.android.lvicto.zombie.keyboard.ZombieApplication.Companion.zombieApplication
import com.android.lvicto.zombie.keyboard.ims.view.key.BaseKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.CandidatesKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.ToggleKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.TypableKeyView
import com.android.lvicto.zombie.keyboard.mock.service.ZombieInputMethodService
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

object KeyListenersMock {

    // todo use injection
    lateinit var mockIms: ZombieInputMethodService

    // used the one in KeyListeners
    fun getKeySuggestionClickListener() = View.OnClickListener { v ->
        val keyView = v as BaseKeyView
        keyView.setPressedUI(true)

        mockIms = InjectorMock.getInstance(zombieApplication).mockIms
        // add suggestion to text
        val suggestion = (keyView as TypableKeyView).getKeyLabel() // todo complete
        mockIms.completeText(suggestion)

        keyView.setPressedUI(false)
    }

    fun getKeyToggleSystemBar() = object : BaseTouchListener() {
        override fun doOnActionDown() {
            mockIms = InjectorMock.getInstance(zombieApplication).mockIms
            mockIms.removeSuggestions()
        }
    }

    fun getKeySettingsTouchListener() = object : BaseTouchListener() {

        override fun doOnActionDown() {
            mockIms = InjectorMock.getInstance(zombieApplication).mockIms
            // start settings activity
            mockIms.goToSettings()
        }
    }

    fun getKeyActionTouchListener() = object : BaseTouchListener() {

        override fun doOnActionDown() {
            keyView.setPressedUI(true)
            // vibrate on press (short)
            vibrate()
            mockIms = InjectorMock.getInstance(zombieApplication).mockIms
            // remove suggestions
            mockIms.removeSuggestions()
            // perform action
            mockIms.performAction()
            // set first letter caps if qwerty and (todo) setting is on
            mockIms.autoCapsOn()
        }
    }

    fun getKeySpaceTouchListener() = object : BaseTouchListener() {

        override fun doOnLongTap() {
            mockIms = InjectorMock.getInstance(zombieApplication).mockIms
            // switch keyboard
            mockIms.switchKeyBoard()
        }

        override fun doOnActionDown() {
            // set pressed
            keyView.setPressedUI(true)
        }

        override fun doOnNormalTapOnly() {
            mockIms = InjectorMock.getInstance(zombieApplication).mockIms
            // type a space
            mockIms.commitText(" ")
        }
    }

    fun getKeyShiftTouchListener() = object : BaseTouchListener() {

        override fun doOnLongTap() {
            (keyView as ToggleKeyView).apply {
                if (!this.isPressedUI()) {
                    mockIms = InjectorMock.getInstance(zombieApplication).mockIms
                    mockIms.setCapsOn()
                }
                this.post {
                    setTogglePersistent(true) // update background
                }
            }
        }

        override fun doOnNormalTapOnly() {
            (keyView as ToggleKeyView).apply {
                mockIms = InjectorMock.getInstance(zombieApplication).mockIms
                if (!(this.isPressedUI() || this.isPersistent)) {
                    this.setPressedUI(true)
                    mockIms.setCapsOn()
                } else {
                    this.setPressedUI(false)
                    mockIms.setCapsOff()

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
                        mockIms = InjectorMock.getInstance(zombieApplication).mockIms
                        // delete selection and set the cursor after previous selection
                        mockIms.deleteCurrentSelection()
                        // toggle first letter if necessary
                        mockIms.autoCapsOn()
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
            mockIms = InjectorMock.getInstance(zombieApplication).mockIms
            // type char
            mockIms.commitText(keyView.text.toString())

            // update suggestions
            val word = mockIms.getCurrentKey()
            mockIms.updateSuggestions(word)
            // reset symbols key
            mockIms.resetSymbolToggle()
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

            mockIms = InjectorMock.getInstance(zombieApplication).mockIms
            // show extras
            mockIms.updateCandidates(keyView as CandidatesKeyView)
            // show digits and reset any toggle
            val defaultSymbolKey = mockIms.keyboardView.getSymbolToggleByCandidates((keyView as TypableKeyView).candidatesResId)
            mockIms.keyboardView.setSymbolTogglePressed(true, defaultSymbolKey)
        }

        override fun doOnActionUp() {
            super.doOnActionUp()

            // hide preview
            (keyView as TypableKeyView).hidePreview()
        }

        override fun doOnNormalTapOnly() {
            Log.d("kissue", "commit text: targetView = ${mockIms.targetView}")

            // type the char
            mockIms.commitText(keyView.text.toString())

            // if caps is on but not toggled, set caps off
            mockIms.setCapsOffIfNotToggled()
            // update suggestions
            val word = mockIms.getCurrentKey()
            mockIms.updateSuggestions(word)
        }
    }

    fun getKeySymbolTouchListener() = object : BaseTouchListener() {

        override fun doOnLongTap() {
            (keyView as ToggleKeyView).apply {
                if (!this.isPressedUI()) {
                    mockIms.updateCandidates(this)
                }
                this.post {
                    mockIms.keyboardView.setSymbolTogglePressed(true, this)
                    setTogglePersistent(true) // update background
                }
            }
        }

        override fun doOnNormalTapOnly() {
            mockIms = InjectorMock.getInstance(zombieApplication).mockIms
            (keyView as ToggleKeyView).apply {
                if (!(this.isPressedUI() || this.isPersistent)) { // its symbols are not shown
                    mockIms.keyboardView.setSymbolTogglePressed(true, this)
                    mockIms.updateCandidates(this)
                    // show toggleDigits
                } else {
                    mockIms.keyboardView.setSymbolTogglePressed(false, this)
                    mockIms.showDigits()
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
        }
    }
}