package com.android.lvicto.sanskritkeyboard

import android.annotation.SuppressLint
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo

@SuppressLint("Registered")
open class StubbedInputMethodService : InputMethodService() {

    override fun onInitializeInterface() {
        Log.d(CustomKeyboard2.LOG_TAG, "onInitializeInterface()")
    }

    override fun onBindInput() {
        Log.d(CustomKeyboard2.LOG_TAG, "onBindInput()")
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        Log.d(CustomKeyboard2.LOG_TAG, "onStartInput()")
    }

    override fun onCreateInputView(): View {
        Log.d(CustomKeyboard2.LOG_TAG, "onCreateInputView()")
        return View(applicationContext)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(CustomKeyboard2.LOG_TAG, "onStartInputView()")
    }

    override fun onRebind(intent: Intent?) {
        Log.d(CustomKeyboard2.LOG_TAG, "onRebind()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(CustomKeyboard2.LOG_TAG, "onStartCommand()")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(CustomKeyboard2.LOG_TAG, "onTaskRemoved()")
        super.onTaskRemoved(rootIntent)
    }

    override fun onTrimMemory(level: Int) {
        Log.d(CustomKeyboard2.LOG_TAG, "onTrimMemory()")
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        Log.d(CustomKeyboard2.LOG_TAG, "onLowMemory()")
        super.onLowMemory()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d(CustomKeyboard2.LOG_TAG, "onStart()")
        super.onStart(intent, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(CustomKeyboard2.LOG_TAG, "onUnbind()")
        return super.onUnbind(intent)
    }

    override fun onViewClicked(focusChanged: Boolean) {
        Log.d(CustomKeyboard2.LOG_TAG, "onViewClicked($focusChanged)")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(CustomKeyboard2.LOG_TAG, "onKeyDown()")
        return super.onKeyDown(keyCode, event)
    }

    override fun onShowInputRequested(flags: Int, configChange: Boolean): Boolean {
        Log.d(CustomKeyboard2.LOG_TAG, "onShowInputRequested()")
        return super.onShowInputRequested(flags, configChange)
    }

    override fun onFinishInput() {
        Log.d(CustomKeyboard2.LOG_TAG, "onFinishInput()")
        super.onFinishInput()
    }

    override fun onWindowShown() {
        Log.d(CustomKeyboard2.LOG_TAG, "onWindowShown()")
        super.onWindowShown()
    }

    override fun onDestroy() {
        Log.d(CustomKeyboard2.LOG_TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun onCreate() {
        Log.d(CustomKeyboard2.LOG_TAG, "onCreate()")
        super.onCreate()
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        Log.d(CustomKeyboard2.LOG_TAG, "onGenericMotionEvent()")
        return super.onGenericMotionEvent(event)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        Log.d(CustomKeyboard2.LOG_TAG, "onFinishInputView()")
        super.onFinishInputView(finishingInput)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(CustomKeyboard2.LOG_TAG, "onKeyUp()")
        return super.onKeyUp(keyCode, event)
    }

}