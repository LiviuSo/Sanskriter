package com.lvicto.skeyboard

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.lvicto.skeyboard.Constants.VIBRATION_INTENSITY
import com.lvicto.skeyboard.Constants.VIBRATION_LEN_LONG
import com.lvicto.skeyboard.Constants.VIBRATION_LEN_SHORT

class KeyboardApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        keyboardApplication = this
    }

    companion object {
        lateinit var keyboardApplication: KeyboardApplication
    }

}

fun Application.isPortrait(): Boolean = this.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT


fun Application.vibrateOnTap(isLongTap: Boolean = false) {
    val vibratorService: Vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (!vibratorService.hasVibrator()) {
        return
    }

    val length: Long = if (isLongTap) {
        VIBRATION_LEN_SHORT
    } else {
        VIBRATION_LEN_LONG
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibratorService.vibrate(VibrationEffect.createOneShot(length, VIBRATION_INTENSITY))
    } else {
        vibratorService.vibrate(length)
    }
}
