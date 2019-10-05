package com.android.lvicto.sanskritkeyboard.newkeyboard.component

import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.android.lvicto.sanskritkeyboard.newkeyboard.SanskritKeyboard
import com.android.lvicto.sanskritkeyboard.utils.Constants.ACTION_SWITCH_KEYBOARD
import com.android.lvicto.sanskritkeyboard.utils.vibrate
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

// todo use injection
class TouchListeners /* todo singleton */ {

    val settingsTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // start settings activity
            keyboard.goToSettings()
        }
    }

    val keyActionTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // set pressed background
            key.setPressed(true)
            // vibrate on press (short)
            keyboard.vibrate()
            // remove suggestions
            keyboard.removeSuggestions()
            // perform action
            keyboard.performAction()
            // set first letter caps
            keyboard.autoCapsOn()
        }

        override fun doOnActionUp() {
            // set normal background
            key.setPressed(false)
        }
    }

    val keySuggestionTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // add suggestion to text
            val suggestion = key.getKeyLabel()
            keyboard.completeText(suggestion)
        }
    }

    val keyToggleDigitsTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // set pressed
            key.setPressed(true)
            // vibrate
            keyboard.vibrate()
            // reset long press keys and show digits
            keyboard.resetToggled()
            // show the digits
            showExtras()
        }
    }

    val keySpaceTouchListener = object : BaseTouchListener() {
        override fun doOnLongTap() {
            // switch keyboard
            context.sendBroadcast(Intent().apply {
                action = ACTION_SWITCH_KEYBOARD
            })
        }

        override fun doOnActionDown() {
            // set pressed
            key.setPressed(true)
        }

        override fun doOnNormalTapOnly() {
            // type a space
            keyboard.commitText(" ")
        }

        override fun doOnActionUp() {
            // set normal
            key.setPressed(false)
        }
    }

    val keyShiftTouchListener = object : BaseTouchListener() {
        override fun doOnLongTap() {
            if(!key.isToggledOn()) {
                // update background
                key.toggleOn(true)
                // set toggle on
                keyboard.toggleCapsOn()
            }
        }

        override fun doOnNormalTapOnly() {
            val capsOn = keyboard.isCapsOn()
            if(capsOn) {
                key.toggleOn(false)
                keyboard.setCapsOn()
            }
            key.setPressed(capsOn)
        }
    }

    val keyDeleteTouchListener = object : BaseTouchListener()  {

        init {
            runnable = {
                var isTimeToRepeat = true
                while (!actionUpFlag.get()) {
                    if (isTimeToRepeat) {
                        // vibrate short
                        context.vibrate()
                        // delete selection and set the cursor after previous selection
                        keyboard.deleteCurrentSelection()
                        // update suggestions
                        keyboard.updateSuggestions()
                        // toggle first letter if necessary
                        keyboard.autoCapsOn()
                        actionTime.set(System.currentTimeMillis()) // reset to detect new repetition
                    }
                    isTimeToRepeat = System.currentTimeMillis() - actionTime.get() > DELAY_AUTO_REPEAT
                }
            }
        }

        override fun doOnActionDown() {
            // set pressed
            key.setPressed(true)
            actionUpFlag.set(false)
        }

        override fun doOnActionUp() {
            // set normal
            key.setPressed(false)
            actionUpFlag.set(true)
        }
    }

    val keyExtraTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            // set backgrd pressed
            key.setPressed(true)
            // type char
            keyboard.commitText(key.getKeyLabel())
            // update suggestions
            keyboard.updateSuggestions()
        }

        override fun doOnActionUp() {
            // set backgrd normal
            key.setPressed(false)
            // toggle digital
            keyboard.updateCandidates()
        }
    }

    val keySansTouchListener: BaseTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            //  set pressed background
            key.setPressed(true)
            // vibrate short
            context.vibrate()
            // show preview
            showPreview()
            // reset any toggle
            keyboard.resetToggled()
            // show extras
            showExtras()
            // type char
            keyboard.commitText(key.getKeyLabel())
        }

        override fun doOnActionUp() {
            // hide preview
            hidePreview()
            // set normal backgrd
            key.setPressed(false)
        }
    }

    val keyQwertyTouchListener = object : BaseTouchListener() {

        override fun doOnLongTap() {
            // vibrate long
            context.vibrate(true)
            // set sticky
            keyboard.updateCandidates(key)
        }

        override fun doOnActionDown() {
            // set pressed backgrd
            key.setPressed(true)
            // vibrate short
            context.vibrate()
            // show preview
            showPreview()
            // show extras
            showExtras()
            // show digits and reset any toggle
            keyboard.resetToggled()
        }

        override fun doOnActionUp() {
            // hide preview
            hidePreview()
            // set normal backgrd
            key.setPressed(false)
        }

        override fun doOnNormalTapOnly() {
            // type the char
            keyboard.commitText(key.getKeyLabel())
            // if caps on but not sticky, set caps off
            if(keyboard.isCapsOn()) {
                keyboard.setCapsOff()
            }
            // update suggestions
            keyboard.updateSuggestions()
        }
    }

    val keySymbolTouchListener = object : BaseTouchListener() {

        override fun doOnActionDown() {
            val isToggled = key.isToggledOn()
            if(!isToggled) {
                keyboard.updateCandidates(key)
            } else {
                keyboard.updateCandidates()
            }
            key.toggleOn(!isToggled)
        }
    }
}

/**
 * Base class for the touch listeners
 */
abstract class BaseTouchListener : View.OnTouchListener {

    private var longTap = AtomicBoolean(false)
    protected lateinit var context: Context
    protected lateinit var key: KeyboardKey
    protected lateinit var keyboard: SanskritKeyboard
    protected var actionTime = AtomicLong(0)
    protected var actionUpFlag = AtomicBoolean(false)

    protected var runnable = {
        actionTime.set(System.currentTimeMillis())
        while (!actionUpFlag.get()) {
            if (System.currentTimeMillis() - actionTime.get() > LONG_PRESS_TIME) {
                longTap.set(true)
                actionUpFlag.set(true) // once long tap reached, end the thread
                doOnLongTap()
            }
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean = when (event.action) {
        MotionEvent.ACTION_DOWN -> {
            key = (view.tag as KeyboardKey)
            keyboard = key.keyboard
            context = keyboard.context

            doOnActionDown()
            Thread(runnable).start() // start thread to catch long taps
            true
        }
        MotionEvent.ACTION_UP -> {
            actionUpFlag.set(true) // stop the thread
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

    protected fun showPreview() {
        key.showPreview()
    }

    protected fun hidePreview() {
        key.hidePreview()
    }

    protected fun showExtras() {
        keyboard.updateCandidates(key)
    }

    @Override
    protected open fun doOnLongTap() {
    }

    @Override
    protected open fun doOnActionDown() {
    }

    @Override
    protected open fun doOnActionUp() {
    }

    @Override
    protected open fun doOnNormalTapOnly() {
    }

    companion object {
        const val DELAY_AUTO_REPEAT: Long = 130
        val LONG_PRESS_TIME = ViewConfiguration.getLongPressTimeout()
    }
}