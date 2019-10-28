package com.android.lvicto.sanskritkeyboard.newkeyboard.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.android.lvicto.sanskritkeyboard.newkeyboard.CustomSanskritKeyboard
import com.android.lvicto.sanskritkeyboard.utils.Constants.ACTION_SWITCH_KEYBOARD

class SwitchKeyboardReceiver(val ims: CustomSanskritKeyboard) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_SWITCH_KEYBOARD) {
            ims.switchKeyboard()
        }
    }

    companion object {
        val intentFilter: IntentFilter
            get() = IntentFilter().apply {
                addAction(ACTION_SWITCH_KEYBOARD)
            }
    }
}