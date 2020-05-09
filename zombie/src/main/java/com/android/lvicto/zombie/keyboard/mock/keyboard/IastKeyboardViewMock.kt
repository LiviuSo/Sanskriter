package com.android.lvicto.zombie.keyboard.mock.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.android.lvicto.zombie.keyboard.mock.KeyListenersMock
import com.android.lvicto.zombie.keyboard.ims.KeyboardType
import com.android.lvicto.zombie.keyboard.ims.view.key.BaseKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.ExtraKeyView
import com.android.lvicto.zombie.keyboard.ims.view.key.ShiftKey
import com.android.lvicto.zombie.keyboard.ims.view.key.TypableKeyView
import com.android.lvicto.zombie.keyboard.ims.view.keyboard.CustomKeyboardView

class IastKeyboardViewMock(context: Context, attributeSet: AttributeSet)
    : CustomKeyboardViewMock(context, attributeSet) {

    override fun setSpaceLabel(view: BaseKeyView) {
        view.text = KeyboardType.IAST.name
    }

    override fun sieveView(view: View): Boolean {
        view.apply {
            when (this) {
                is TypableKeyView -> {
                    if (this !is ExtraKeyView) {
                        if (this is ShiftKey) {
                            addShiftableKetView(this)
                        }
                        this.setOnTouchListener(KeyListenersMock.getKeyTypableTouchListener())
                        return true
                    } else {
                        if (this is ShiftKey) { // is extra, but also shiftable
                            addShiftableKetView(this)
                            addExtraKeyView(this)
                            this.setOnTouchListener(KeyListenersMock.getKeyExtraTouchListener())
                            return true
                        }
                    }
                }
            }
            return false
        }
    }
}