package com.android.lvicto.sanskritkeyboard.keyboard

import android.content.Context
import android.view.View
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.utils.button
import com.android.lvicto.sanskritkeyboard.utils.layoutInflater

open class KbLayoutInitializerPhonePortraitSa(context: Context) : KbLayoutInitializer(context) {

    override fun initExtraCodes() {
        val res = context.resources
        val codes = arrayListOf(
                res.getInteger(R.integer.key_code_sa_ka)
                , res.getInteger(R.integer.key_code_sa_kha)
                , res.getInteger(R.integer.key_code_sa_ga)
                , res.getInteger(R.integer.key_code_sa_gha)
                , res.getInteger(R.integer.key_code_sa_ca)
                , res.getInteger(R.integer.key_code_sa_cha)
                , res.getInteger(R.integer.key_code_sa_ja)
                , res.getInteger(R.integer.key_code_sa_jha)
                , res.getInteger(R.integer.key_code_sa_ta2)
                , res.getInteger(R.integer.key_code_sa_tha2)
                , res.getInteger(R.integer.key_code_sa_da2)
                , res.getInteger(R.integer.key_code_sa_dha2)
                , res.getInteger(R.integer.key_code_sa_ta)
                , res.getInteger(R.integer.key_code_sa_tha)
                , res.getInteger(R.integer.key_code_sa_da)
                , res.getInteger(R.integer.key_code_sa_dha)
                , res.getInteger(R.integer.key_code_sa_pa)
                , res.getInteger(R.integer.key_code_sa_pha)
                , res.getInteger(R.integer.key_code_sa_ba)
                , res.getInteger(R.integer.key_code_sa_bha)
                , res.getInteger(R.integer.key_code_sa_na1)
                , res.getInteger(R.integer.key_code_sa_na2)
                , res.getInteger(R.integer.key_code_sa_na3)
                , res.getInteger(R.integer.key_code_sa_na4)
                , res.getInteger(R.integer.key_code_sa_ma)
                , res.getInteger(R.integer.key_code_sa_sa1)
                , res.getInteger(R.integer.key_code_sa_sa2)
                , res.getInteger(R.integer.key_code_sa_sa3)
                , res.getInteger(R.integer.key_code_sa_ya)
                , res.getInteger(R.integer.key_code_sa_ra)
                , res.getInteger(R.integer.key_code_sa_la)
                , res.getInteger(R.integer.key_code_sa_va)
                , res.getInteger(R.integer.key_code_sa_h)
        )
        codes.forEach {
            extraKeysCodesMap[it] = arrayListOf(
                    res.getInteger(R.integer.key_code_extra_red)
                    , res.getInteger(R.integer.key_code_extra_long_a)
                    , res.getInteger(R.integer.key_code_extra_i)
                    , res.getInteger(R.integer.key_code_extra_long_i)
                    , res.getInteger(R.integer.key_code_extra_u)
                    , res.getInteger(R.integer.key_code_extra_long_u)
                    , res.getInteger(R.integer.key_code_extra_ri)
                    , res.getInteger(R.integer.key_code_extra_long_lri)
                    , res.getInteger(R.integer.key_code_extra_e)
                    , res.getInteger(R.integer.key_code_extra_ai)
                    , res.getInteger(R.integer.key_code_extra_o)
                    , res.getInteger(R.integer.key_code_extra_au)
                    , res.getInteger(R.integer.key_code_extra_anushvara)
                    , res.getInteger(R.integer.key_code_extra_visarga)
                    , res.getInteger(R.integer.key_code_extra_omkara1)
                    , res.getInteger(R.integer.key_code_extra_omkara2)
            )
        }
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_stop)] = arrayListOf(res.getInteger(R.integer.key_code_sa_double_stop))
    }

    override fun getView(): View =
            context.layoutInflater().inflate(R.layout.keyboard_phone_portrait_sa, null)

    override fun bindKeys(view: View) {
        super.bindKeys(view)
        val keysClickListener = arrayListOf(
                view button R.id.keySaDigit1
                , view button R.id.keySaDigit2
                , view button R.id.keySaDigit3
                , view button R.id.keySaDigit4
                , view button R.id.keySaDigit5
                , view button R.id.keySaDigit6
                , view button R.id.keySaDigit7
                , view button R.id.keySaDigit8
                , view button R.id.keySaDigit9
                , view button R.id.keySaDigit0
                , view button R.id.keySaA
                , view button R.id.keySaI
                , view button R.id.keySaU
                , view button R.id.keySaRi
                , view button R.id.keySaLri
                , view button R.id.keySaE
                , view button R.id.keySaO
                , view button R.id.keySaAccent1
                , view button R.id.keySaAccent2
                , view button R.id.key_label_sa_apostrophy
                , view button R.id.keySaKa
                , view button R.id.keySaKha
                , view button R.id.keySaGa
                , view button R.id.keySaGha
                , view button R.id.keySaCa
                , view button R.id.keySaCha
                , view button R.id.keySaJa
                , view button R.id.keySaJha
                , view button R.id.keySaTa2
                , view button R.id.keySaTha2
                , view button R.id.keySaDa2
                , view button R.id.keySaDha2
                , view button R.id.keySaTa
                , view button R.id.keySaTha
                , view button R.id.keySaDa
                , view button R.id.keySaDha
                , view button R.id.keySaPa
                , view button R.id.keySaPha
                , view button R.id.keySaBa
                , view button R.id.keySaBha
                , view button R.id.keySaNa1
                , view button R.id.keySaNa2
                , view button R.id.keySaNa3
                , view button R.id.keySaNa4
                , view button R.id.keySaMa
                , view button R.id.keySaSa1
                , view button R.id.keySaSa2
                , view button R.id.keySaSa3
                , view button R.id.keySaYa
                , view button R.id.keySaRa
                , view button R.id.keySaLa
                , view button R.id.keySaVa
                , view button R.id.keySaHa
                , view button R.id.keySaSep

        )
        keysClickListener.forEach {
            it.setOnTouchListener(getCommonTouchListener())
        }

        initExtraKeys(view)
    }
}
