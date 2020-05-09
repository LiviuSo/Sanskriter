package com.android.lvicto.zombie.keyboard.mock.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.android.lvicto.zombie.keyboard.mock.KeyListenersMock
import com.android.lvicto.zombie.keyboard.ims.KeyboardType
import com.android.lvicto.zombie.keyboard.ims.view.key.BaseKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.ExtraKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.SanskritShiftKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.TypableKeyView
import com.android.lvicto.zombie.keyboard.ims.view.keyboard.CustomKeyboardView

class SanskritKeyboardViewMock(context: Context, attributeSet: AttributeSet)
    : CustomKeyboardViewMock(context, attributeSet) {

    override fun setSpaceLabel(view: BaseKeyView) {
        view.text = KeyboardType.SA.name
    }

    override fun sieveView(view: View): Boolean {
        when (view) {
            is TypableKeyView -> {
                if (view !is ExtraKeyView) {
                    if (view is SanskritShiftKeyView) {
                        addShiftableKetView(view)
                    }
                    view.setOnTouchListener(KeyListenersMock.getKeyTypableTouchListener())
                    return true
                }
            }
        }
        return false
    }
}