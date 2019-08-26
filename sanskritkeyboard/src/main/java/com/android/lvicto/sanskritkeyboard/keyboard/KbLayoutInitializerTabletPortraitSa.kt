package com.android.lvicto.sanskritkeyboard.keyboard

import android.content.Context
import android.view.View
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.utils.layoutInflater

open class KbLayoutInitializerTabletPortraitSa(context: Context) : KbLayoutInitializerPhonePortraitSa(context) {

    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_tablet_portrait_sa, null)

}