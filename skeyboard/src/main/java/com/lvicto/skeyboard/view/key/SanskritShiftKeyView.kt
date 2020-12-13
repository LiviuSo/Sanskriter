package com.lvicto.skeyboard.view.key

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.IntegerRes
import com.example.skeyboard.R

class SanskritShiftKeyView(context: Context, attr: AttributeSet)
    : TypableKeyView(context, attr), ShiftKey {

    private var isAspirateToggled = false

    override fun shiftKey(shifted: Boolean) {
        this.apply {
            isAspirateToggled = shifted
            val key = this as TextView
            this.post {
                key.text = if (shifted) {
                    val aspirateResId = getAspiratePair(this.labelResId)
                    this.labelResId = aspirateResId
                    context.getString(aspirateResId)
                } else {
                    val nonAspirateResId = getNonAspiratePair(this.labelResId)
                    this.labelResId = nonAspirateResId
                    context.getString(nonAspirateResId)
                }
            }
        }
    }

    private fun getAspiratePair(@IntegerRes nonAspirate: Int): Int {
        return when (nonAspirate) {
            R.string.key_label_sa_letter_ka -> R.string.key_label_sa_letter_kha
            R.string.key_label_sa_letter_ga -> R.string.key_label_sa_letter_gha
            R.string.key_label_sa_letter_ca -> R.string.key_label_sa_letter_cha
            R.string.key_label_sa_letter_ja -> R.string.key_label_sa_letter_jha
            R.string.key_label_sa_letter_ta2 -> R.string.key_label_sa_letter_tha2
            R.string.key_label_sa_letter_da2 -> R.string.key_label_sa_letter_dha2
            R.string.key_label_sa_letter_ta -> R.string.key_label_sa_letter_tha
            R.string.key_label_sa_letter_da -> R.string.key_label_sa_letter_dha
            R.string.key_label_sa_letter_pa -> R.string.key_label_sa_letter_pha
            R.string.key_label_sa_letter_ba -> R.string.key_label_sa_letter_bha
            else -> -1
        }
    }

    private fun getNonAspiratePair(@IntegerRes aspirate: Int): Int {
        return when (aspirate) {
            R.string.key_label_sa_letter_kha -> R.string.key_label_sa_letter_ka
            R.string.key_label_sa_letter_gha -> R.string.key_label_sa_letter_ga
            R.string.key_label_sa_letter_cha -> R.string.key_label_sa_letter_ca
            R.string.key_label_sa_letter_jha -> R.string.key_label_sa_letter_ja
            R.string.key_label_sa_letter_tha2 -> R.string.key_label_sa_letter_ta2
            R.string.key_label_sa_letter_dha2 -> R.string.key_label_sa_letter_da2
            R.string.key_label_sa_letter_tha -> R.string.key_label_sa_letter_ta
            R.string.key_label_sa_letter_dha -> R.string.key_label_sa_letter_da
            R.string.key_label_sa_letter_pha -> R.string.key_label_sa_letter_pa
            R.string.key_label_sa_letter_bha -> R.string.key_label_sa_letter_ba
            else -> -1
        }
    }
}