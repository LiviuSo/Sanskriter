package com.android.lvicto.sanskritkeyboard.newkeyboard.helpers

import android.content.Context
import android.content.res.Resources
import com.android.lvicto.sanskritkeyboard.R
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardConfig
import com.android.lvicto.sanskritkeyboard.keyboard.KeyboardType
import com.android.lvicto.sanskritkeyboard.newkeyboard.component.KeyboardKey
import com.android.lvicto.sanskritkeyboard.utils.getVal


/**
 * Provides data related to the keys
 */
class KeysFactoryHelper(val context: Context) {
    val res: Resources = context.resources

    fun getKeys(config: KeyboardConfig): Map<Int, KeyboardKey> =
            if (config.type == KeyboardType.QWERTY) {
                getQwertyKeys()
            } else {
                getSanskritKeys()
            }

    fun getCandidatesMap(config: KeyboardConfig): Map<Int, List<Int>> =
            if (config.type == KeyboardType.QWERTY) {
                qwertyCandidatesCodesMap
            } else {
                sanskritCandidatesCodesMap
            }

    fun getCodeFromId(id: Int, config: KeyboardConfig): Int =
            if (config.type == KeyboardType.QWERTY) {
                val res = qwertyCodesToIds.filter {
                    it.value == id
                }.keys

                if (res.isEmpty()) {
                    qwertyExtraCodesToIds.filter {
                        it.value == id
                    }.keys.first()
                } else {
                    res.first()
                }
            } else {
                val res = sanskritCodesToIds.filter {
                    it.value == id
                }.keys

                if (res.isEmpty()) {
                    sanskritExtraCodesToIds.filter {
                        it.value == id
                    }.keys.first()
                } else {
                    res.first()
                }
            }

    private fun getQwertyKeys() = hashMapOf<Int, KeyboardKey>().apply {
        qwertyCodesToIds.keys.forEach { code ->
            this[code] = KeyboardKey(code, qwertyCandidatesCodesMap[code])
        }
        qwertyExtraCodesToIds.keys.forEach { code ->
            this[code] = KeyboardKey(code, qwertyCandidatesCodesMap[code], R.drawable.key_extra_normal,
                    R.drawable.key_extra_pressed)
        }
    }

    private fun getSanskritKeys() = hashMapOf<Int, KeyboardKey>().apply {
        sanskritCodesToIds.keys.forEach { code ->
            this[code] = KeyboardKey(code, sanskritCandidatesCodesMap[code])
        }
        sanskritExtraCodesToIds.keys.forEach { code ->
            this[code] = KeyboardKey(code, sanskritCandidatesCodesMap[code], R.drawable.key_extra_normal,
                    R.drawable.key_extra_pressed)
        }
    }

    fun getDigitsCodes(config: KeyboardConfig) = if (config.type == KeyboardType.QWERTY) {
        arrayListOf(
                res.getInteger(R.integer.key_code_digit_1),
                res.getInteger(R.integer.key_code_digit_2),
                res.getInteger(R.integer.key_code_digit_3),
                res.getInteger(R.integer.key_code_digit_4),
                res.getInteger(R.integer.key_code_digit_5),
                res.getInteger(R.integer.key_code_digit_6),
                res.getInteger(R.integer.key_code_digit_7),
                res.getInteger(R.integer.key_code_digit_8),
                res.getInteger(R.integer.key_code_digit_9),
                res.getInteger(R.integer.key_code_digit_0)
        )
    } else {
        arrayListOf(
                res.getInteger(R.integer.key_code_sa_digit_1),
                res.getInteger(R.integer.key_code_sa_digit_2),
                res.getInteger(R.integer.key_code_sa_digit_3),
                res.getInteger(R.integer.key_code_sa_digit_4),
                res.getInteger(R.integer.key_code_sa_digit_5),
                res.getInteger(R.integer.key_code_sa_digit_6),
                res.getInteger(R.integer.key_code_sa_digit_7),
                res.getInteger(R.integer.key_code_sa_digit_8),
                res.getInteger(R.integer.key_code_sa_digit_9),
                res.getInteger(R.integer.key_code_sa_digit_0)
        )
    }

    val DIGIT_1 = res.getInteger(R.integer.key_code_digit_1)
    val DIGIT_2 = res.getInteger(R.integer.key_code_digit_2)
    val DIGIT_3 = res.getInteger(R.integer.key_code_digit_3)
    val DIGIT_4 = res.getInteger(R.integer.key_code_digit_4)
    val DIGIT_5 = res.getInteger(R.integer.key_code_digit_5)
    val DIGIT_6 = res.getInteger(R.integer.key_code_digit_6)
    val DIGIT_7 = res.getInteger(R.integer.key_code_digit_7)
    val DIGIT_8 = res.getInteger(R.integer.key_code_digit_8)
    val DIGIT_9 = res.getInteger(R.integer.key_code_digit_9)
    val DIGIT_0 = res.getInteger(R.integer.key_code_digit_0)

    val DIGIT_SA_1 = res.getInteger(R.integer.key_code_sa_digit_1)
    val DIGIT_SA_2 = res.getInteger(R.integer.key_code_sa_digit_2)
    val DIGIT_SA_3 = res.getInteger(R.integer.key_code_sa_digit_3)
    val DIGIT_SA_4 = res.getInteger(R.integer.key_code_sa_digit_4)
    val DIGIT_SA_5 = res.getInteger(R.integer.key_code_sa_digit_5)
    val DIGIT_SA_6 = res.getInteger(R.integer.key_code_sa_digit_6)
    val DIGIT_SA_7 = res.getInteger(R.integer.key_code_sa_digit_7)
    val DIGIT_SA_8 = res.getInteger(R.integer.key_code_sa_digit_8)
    val DIGIT_SA_9 = res.getInteger(R.integer.key_code_sa_digit_9)
    val DIGIT_SA_0 = res.getInteger(R.integer.key_code_sa_digit_0)

    val TOGGLE_KEYS = res.getInteger(R.integer.key_code_toggle_digits)

    private val qwertyCodesToIds = hashMapOf<Int, Int>().apply {
        this[res.getInteger(R.integer.key_code_letter_q)] = R.id.keyQ
        this[res.getInteger(R.integer.key_code_letter_w)] = R.id.keyW
        this[res.getInteger(R.integer.key_code_letter_e)] = R.id.keyE
        this[res.getInteger(R.integer.key_code_letter_r)] = R.id.keyR
        this[res.getInteger(R.integer.key_code_letter_t)] = R.id.keyT
        this[res.getInteger(R.integer.key_code_letter_y)] = R.id.keyY
        this[res.getInteger(R.integer.key_code_letter_u)] = R.id.keyU
        this[res.getInteger(R.integer.key_code_letter_i)] = R.id.keyI
        this[res.getInteger(R.integer.key_code_letter_o)] = R.id.keyO
        this[res.getInteger(R.integer.key_code_letter_p)] = R.id.keyP

        this[res.getInteger(R.integer.key_code_letter_a)] = R.id.keyA
        this[res.getInteger(R.integer.key_code_letter_s)] = R.id.keyS
        this[res.getInteger(R.integer.key_code_letter_d)] = R.id.keyD
        this[res.getInteger(R.integer.key_code_letter_f)] = R.id.keyF
        this[res.getInteger(R.integer.key_code_letter_g)] = R.id.keyG
        this[res.getInteger(R.integer.key_code_letter_h)] = R.id.keyH
        this[res.getInteger(R.integer.key_code_letter_j)] = R.id.keyJ
        this[res.getInteger(R.integer.key_code_letter_k)] = R.id.keyK
        this[res.getInteger(R.integer.key_code_letter_l)] = R.id.keyL

        this[res.getInteger(R.integer.key_code_letter_z)] = R.id.keyZ
        this[res.getInteger(R.integer.key_code_letter_x)] = R.id.keyX
        this[res.getInteger(R.integer.key_code_letter_c)] = R.id.keyC
        this[res.getInteger(R.integer.key_code_letter_v)] = R.id.keyV
        this[res.getInteger(R.integer.key_code_letter_b)] = R.id.keyB
        this[res.getInteger(R.integer.key_code_letter_n)] = R.id.keyN
        this[res.getInteger(R.integer.key_code_letter_m)] = R.id.keyM

        this[res.getInteger(R.integer.key_code_shift)] = R.id.keyShift
        this[res.getInteger(R.integer.key_code_symbols_punctuation)] = R.id.keyPunctuation
        this[res.getInteger(R.integer.key_code_symbols_braces)] = R.id.keyBraces
        this[res.getInteger(R.integer.key_code_symbols)] = R.id.keySymbol
        this[res.getInteger(R.integer.key_code_backspace)] = R.id.keyDel
        this[res.getInteger(R.integer.key_code_action)] = R.id.keyAction
        this[res.getInteger(R.integer.key_code_space)] = R.id.keySpace
        this[res.getInteger(R.integer.key_code_settings)] = R.id.keySettings

        this[res.getInteger(R.integer.key_code_suggestion1)] = R.id.keySuggestion1
        this[res.getInteger(R.integer.key_code_suggestion2)] = R.id.keySuggestion2
        this[res.getInteger(R.integer.key_code_suggestion3)] = R.id.keySuggestion3
    } // todo create constants with the coded

    private val qwertyExtraCodesToIds: Map<Int, Int> = hashMapOf<Int, Int>().apply {
        // digits
        this[DIGIT_1] = R.id.keyLetterExtra1
        this[DIGIT_2] = R.id.keyLetterExtra2
        this[DIGIT_3] = R.id.keyLetterExtra3
        this[DIGIT_4] = R.id.keyLetterExtra4
        this[DIGIT_5] = R.id.keyLetterExtra5
        this[DIGIT_6] = R.id.keyLetterExtra6
        this[DIGIT_7] = R.id.keyLetterExtra7
        this[DIGIT_8] = R.id.keyLetterExtra8
        this[DIGIT_9] = R.id.keyLetterExtra9
        this[DIGIT_0] = R.id.keyLetterExtra10

        this[TOGGLE_KEYS] = R.id.keyToggleDigits

        this[res.getInteger(R.integer.key_code_question_mark)] = 0
        this[res.getInteger(R.integer.key_code_exclamation_mark)] = 0
        this[res.getInteger(R.integer.key_code_comma)] = 0

        this[res.getInteger(R.integer.key_code_apostrophy)] = 0
        this[res.getInteger(R.integer.key_code_quote)] = 0
        this[res.getInteger(R.integer.key_code_semicolon)] = 0
        this[res.getInteger(R.integer.key_code_colon)] = 0

        this[res.getInteger(R.integer.key_code_open_paranthesis)] = 0
        this[res.getInteger(R.integer.key_code_close_paranthesis)] = 0
        this[res.getInteger(R.integer.key_code_open_square_paranthesis)] = 0
        this[res.getInteger(R.integer.key_code_close_square_paranthesis)] = 0
        this[res.getInteger(R.integer.key_code_open_curly_brace)] = 0
        this[res.getInteger(R.integer.key_code_close_curly_brace)] = 0

        this[res.getInteger(R.integer.key_code_period)] = 0
        this[res.getInteger(R.integer.key_code_hyphen)] = 0
        this[res.getInteger(R.integer.key_code_ampersand)] = 0
        this[res.getInteger(R.integer.key_code_at)] = 0

        this[res.getInteger(R.integer.key_code_plus)] = 0
        this[res.getInteger(R.integer.key_code_multiplication)] = 0
        this[res.getInteger(R.integer.key_code_division)] = 0
        this[res.getInteger(R.integer.key_code_lt)] = 0
        this[res.getInteger(R.integer.key_code_gt)] = 0
        this[res.getInteger(R.integer.key_code_equal)] = 0
        this[res.getInteger(R.integer.key_code_xor)] = 0
        this[res.getInteger(R.integer.key_code_percent)] = 0
        this[res.getInteger(R.integer.key_code_tilda)] = 0

        this[res.getInteger(R.integer.key_code_underscore)] = 0
        this[res.getInteger(R.integer.key_code_slash)] = 0
        this[res.getInteger(R.integer.key_code_backslash)] = 0
        this[res.getInteger(R.integer.key_code_pipe)] = 0
        this[res.getInteger(R.integer.key_code_back_apostrophe)] = 0
        this[res.getInteger(R.integer.key_code_dollar)] = 0
        this[res.getInteger(R.integer.key_code_pound)] = 0
        this[res.getInteger(R.integer.key_code_cent)] = 0

        this[res.getInteger(R.integer.key_code_letter_a_ro1)] = 0
        this[res.getInteger(R.integer.key_code_letter_a_ro2)] = 0
        this[res.getInteger(R.integer.key_code_letter_a_sa)] = 0
        this[res.getInteger(R.integer.key_code_letter_i_ro)] = 0
        this[res.getInteger(R.integer.key_code_letter_i_sa)] = 0
        this[res.getInteger(R.integer.key_code_letter_l_sa)] = 0
        this[res.getInteger(R.integer.key_code_letter_l_sa_long)] = 0
        this[res.getInteger(R.integer.key_code_letter_m_sa)] = 0
        this[res.getInteger(R.integer.key_code_letter_m2_sa)] = 0
        this[res.getInteger(R.integer.key_code_letter_n_sa1)] = 0
        this[res.getInteger(R.integer.key_code_letter_n_sa2)] = 0
        this[res.getInteger(R.integer.key_code_letter_n_sa3)] = 0
        this[res.getInteger(R.integer.key_code_letter_r_sa1)] = 0
        this[res.getInteger(R.integer.key_code_letter_r_sa2)] = 0
        this[res.getInteger(R.integer.key_code_letter_s_ro)] = 0
        this[res.getInteger(R.integer.key_code_letter_s_sa1)] = 0
        this[res.getInteger(R.integer.key_code_letter_s_sa2)] = 0
        this[res.getInteger(R.integer.key_code_letter_t_ro)] = 0
        this[res.getInteger(R.integer.key_code_letter_t_sa)] = 0
        this[res.getInteger(R.integer.key_code_letter_d_sa)] = 0
        this[res.getInteger(R.integer.key_code_letter_u_sa)] = 0
        this[res.getInteger(R.integer.key_code_letter_h_sa)] = 0

        this[res.getInteger(R.integer.key_code_extra_accent1)] = 0
        this[res.getInteger(R.integer.key_code_extra_accent2)] = 0
    }

    // extras
    private val sanskritCodesToIds: Map<Int, Int> = hashMapOf<Int, Int>().apply {
        this[res.getInteger(R.integer.key_code_sa_a)] = R.id.keySaA
        this[res.getInteger(R.integer.key_code_sa_i)] = R.id.keySaI
        this[res.getInteger(R.integer.key_code_sa_u)] = R.id.keySaU
        this[res.getInteger(R.integer.key_code_sa_ri)] = R.id.keySaRi
        this[res.getInteger(R.integer.key_code_sa_lri)] = R.id.keySaLri
        this[res.getInteger(R.integer.key_code_sa_e)] = R.id.keySaE
        this[res.getInteger(R.integer.key_code_sa_o)] = R.id.keySaO
        this[res.getInteger(R.integer.key_code_sa_h)] = R.id.keySaHa

        this[res.getInteger(R.integer.key_code_sa_ka)] = R.id.keySaKa
        this[res.getInteger(R.integer.key_code_sa_kha)] = R.id.keySaKha
        this[res.getInteger(R.integer.key_code_sa_ga)] = R.id.keySaGa
        this[res.getInteger(R.integer.key_code_sa_gha)] = R.id.keySaGha
        this[res.getInteger(R.integer.key_code_sa_ca)] = R.id.keySaCa
        this[res.getInteger(R.integer.key_code_sa_cha)] = R.id.keySaCha
        this[res.getInteger(R.integer.key_code_sa_ja)] = R.id.keySaJa
        this[res.getInteger(R.integer.key_code_sa_jha)] = R.id.keySaJha
        this[res.getInteger(R.integer.key_code_sa_ta2)] = R.id.keySaTa2
        this[res.getInteger(R.integer.key_code_sa_tha2)] = R.id.keySaTha2

        this[res.getInteger(R.integer.key_code_sa_da2)] = R.id.keySaDa2
        this[res.getInteger(R.integer.key_code_sa_dha2)] = R.id.keySaDha2
        this[res.getInteger(R.integer.key_code_sa_ta)] = R.id.keySaTa
        this[res.getInteger(R.integer.key_code_sa_tha)] = R.id.keySaTha
        this[res.getInteger(R.integer.key_code_sa_da)] = R.id.keySaDa
        this[res.getInteger(R.integer.key_code_sa_dha)] = R.id.keySaDha
        this[res.getInteger(R.integer.key_code_sa_pa)] = R.id.keySaPa
        this[res.getInteger(R.integer.key_code_sa_pha)] = R.id.keySaPha
        this[res.getInteger(R.integer.key_code_sa_ba)] = R.id.keySaBa
        this[res.getInteger(R.integer.key_code_sa_bha)] = R.id.keySaBha

        this[res.getInteger(R.integer.key_code_sa_na1)] = R.id.keySaNa1
        this[res.getInteger(R.integer.key_code_sa_na2)] = R.id.keySaNa2
        this[res.getInteger(R.integer.key_code_sa_na3)] = R.id.keySaNa3
        this[res.getInteger(R.integer.key_code_sa_na4)] = R.id.keySaNa4
        this[res.getInteger(R.integer.key_code_sa_ma)] = R.id.keySaMa
        this[res.getInteger(R.integer.key_code_sa_sa1)] = R.id.keySaSa1
        this[res.getInteger(R.integer.key_code_sa_sa2)] = R.id.keySaSa2
        this[res.getInteger(R.integer.key_code_sa_sa3)] = R.id.keySaSa3

        this[res.getInteger(R.integer.key_code_sa_ya)] = R.id.keySaYa
        this[res.getInteger(R.integer.key_code_sa_ra)] = R.id.keySaRa
        this[res.getInteger(R.integer.key_code_sa_la)] = R.id.keySaLa
        this[res.getInteger(R.integer.key_code_sa_va)] = R.id.keySaVa

        this[res.getInteger(R.integer.key_code_space)] = R.id.keySpace
        this[res.getInteger(R.integer.key_code_action)] = R.id.keyAction
        this[res.getInteger(R.integer.key_code_symbols)] = R.id.keySymbol
        this[res.getInteger(R.integer.key_code_backspace)] = R.id.keyDel
        this[res.getInteger(R.integer.key_code_sap_sep)] = R.id.keySaSep
        this[res.getInteger(R.integer.key_code_apostrophy)] = R.id.keySaApostrophy
        this[res.getInteger(R.integer.key_code_settings)] = R.id.keySettings
        this[res.getInteger(R.integer.key_code_suggestion1)] = R.id.keySuggestion1
        this[res.getInteger(R.integer.key_code_suggestion2)] = R.id.keySuggestion2
        this[res.getInteger(R.integer.key_code_suggestion3)] = R.id.keySuggestion3
    }

    private val sanskritExtraCodesToIds: Map<Int, Int> = hashMapOf<Int, Int>().apply {
        // digits
        this[DIGIT_SA_1] = R.id.keyLetterExtra1
        this[DIGIT_SA_2] = R.id.keyLetterExtra2
        this[DIGIT_SA_3] = R.id.keyLetterExtra3
        this[DIGIT_SA_4] = R.id.keyLetterExtra4
        this[DIGIT_SA_5] = R.id.keyLetterExtra5
        this[DIGIT_SA_6] = R.id.keyLetterExtra6
        this[DIGIT_SA_7] = R.id.keyLetterExtra7
        this[DIGIT_SA_8] = R.id.keyLetterExtra8
        this[DIGIT_SA_9] = R.id.keyLetterExtra9
        this[DIGIT_SA_0] = R.id.keyLetterExtra10

        this[TOGGLE_KEYS] = R.id.keyToggleDigits

        // extras
        this[res.getInteger(R.integer.key_code_extra_red)] = 0
        this[res.getInteger(R.integer.key_code_extra_apostrophy)] = 0
        this[res.getInteger(R.integer.key_code_extra_omkara1)] = 0
        this[res.getInteger(R.integer.key_code_extra_omkara2)] = 0
        this[res.getInteger(R.integer.key_code_extra_anushvara)] = 0
        this[res.getInteger(R.integer.key_code_extra_visarga)] = 0
        this[res.getInteger(R.integer.key_code_extra_long_a)] = 0
        this[res.getInteger(R.integer.key_code_extra_i)] = 0
        this[res.getInteger(R.integer.key_code_extra_long_i)] = 0
        this[res.getInteger(R.integer.key_code_extra_u)] = 0
        this[res.getInteger(R.integer.key_code_extra_long_u)] = 0

        this[res.getInteger(R.integer.key_code_extra_ri)] = 0
        this[res.getInteger(R.integer.key_code_extra_long_ri)] = 0
        this[res.getInteger(R.integer.key_code_extra_lri)] = 0
        this[res.getInteger(R.integer.key_code_extra_long_lri)] = 0
        this[res.getInteger(R.integer.key_code_extra_e)] = 0
        this[res.getInteger(R.integer.key_code_extra_ai)] = 0
        this[res.getInteger(R.integer.key_code_extra_o)] = 0
        this[res.getInteger(R.integer.key_code_extra_au)] = 0
        this[res.getInteger(R.integer.key_code_extra_accent1)] = 0
        this[res.getInteger(R.integer.key_code_extra_accent2)] = 0

        this[res.getInteger(R.integer.key_code_sa_double_stop)] = 0
        this[res.getInteger(R.integer.key_code_sa_long_a)] = 0
        this[res.getInteger(R.integer.key_code_sa_long_i)] = 0
        this[res.getInteger(R.integer.key_code_sa_long_u)] = 0
        this[res.getInteger(R.integer.key_code_sa_long_ri)] = 0
        this[res.getInteger(R.integer.key_code_sa_long_lri)] = 0
        this[res.getInteger(R.integer.key_code_sa_ai)] = 0
        this[res.getInteger(R.integer.key_code_sa_au)] = 0
    }

    private val sanskritCandidatesCodesMap = hashMapOf<Int, List<Int>>().apply {
        val extraKeysCodesMap = this
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
        val commons = arrayListOf(
                res.getInteger(R.integer.key_code_extra_long_a) // todo extras on the letter
                , res.getInteger(R.integer.key_code_extra_i)
                , res.getInteger(R.integer.key_code_extra_long_i)
                , res.getInteger(R.integer.key_code_extra_u)
                , res.getInteger(R.integer.key_code_extra_long_u)
                , res.getInteger(R.integer.key_code_extra_ri)
                , res.getInteger(R.integer.key_code_extra_e)
                , res.getInteger(R.integer.key_code_extra_ai)
                , res.getInteger(R.integer.key_code_extra_o)
                , res.getInteger(R.integer.key_code_extra_au)
        )
        codes.forEach {
            extraKeysCodesMap[it] = commons
        }
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_stop)] = arrayListOf(res.getInteger(R.integer.key_code_sa_double_stop))
        extraKeysCodesMap[res.getInteger(R.integer.key_code_symbols)] = arrayListOf(
                res.getInteger(R.integer.key_code_extra_red)
                , res.getInteger(R.integer.key_code_extra_anushvara)
                , res.getInteger(R.integer.key_code_extra_visarga)
                , res.getInteger(R.integer.key_code_extra_omkara1)
                , res.getInteger(R.integer.key_code_extra_omkara2)
                , res.getInteger(R.integer.key_code_extra_long_lri)
                , res.getInteger(R.integer.key_code_extra_accent1)
                , res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_a)] = arrayListOf(
                res.getInteger(R.integer.key_code_sa_long_a)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_i)] = arrayListOf(
                res.getInteger(R.integer.key_code_sa_long_i)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_u)] = arrayListOf(
                res.getInteger(R.integer.key_code_sa_long_u)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_ri)] = arrayListOf(
                res.getInteger(R.integer.key_code_sa_long_ri)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_lri)] = arrayListOf(
                res.getInteger(R.integer.key_code_sa_long_lri)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_e)] = arrayListOf(
                res.getInteger(R.integer.key_code_sa_ai)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_sa_o)] = arrayListOf(
                res.getInteger(R.integer.key_code_sa_au)
        )
    }

    private val qwertyCandidatesCodesMap = hashMapOf<Int, List<Int>>().apply {
        val extraKeysCodesMap = this
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_a)] = arrayListOf(
                R.integer.key_code_letter_a_ro1.getVal(context),
                res.getInteger(R.integer.key_code_letter_a_ro2),
                res.getInteger(R.integer.key_code_letter_a_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_A)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_A_ro1),
                res.getInteger(R.integer.key_code_letter_A_ro2),
                res.getInteger(R.integer.key_code_letter_A_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_i)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_i_ro),
                res.getInteger(R.integer.key_code_letter_i_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_I)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_I_ro),
                res.getInteger(R.integer.key_code_letter_I_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_l)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_l_sa),
                res.getInteger(R.integer.key_code_letter_l_sa_long),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_L)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_L_sa),
                res.getInteger(R.integer.key_code_letter_L_sa_long),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_m)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_m_sa),
                res.getInteger(R.integer.key_code_letter_m2_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_M)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_M_sa),
                res.getInteger(R.integer.key_code_letter_M2_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_n)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_n_sa1),
                res.getInteger(R.integer.key_code_letter_n_sa2),
                res.getInteger(R.integer.key_code_letter_n_sa3),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_N)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_N_sa1),
                res.getInteger(R.integer.key_code_letter_N_sa2),
                res.getInteger(R.integer.key_code_letter_N_sa3),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_r)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_r_sa1),
                res.getInteger(R.integer.key_code_letter_r_sa2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_R)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_R_sa1),
                res.getInteger(R.integer.key_code_letter_R_sa2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_s)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_s_ro),
                res.getInteger(R.integer.key_code_letter_s_sa1),
                res.getInteger(R.integer.key_code_letter_s_sa2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_S)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_S_ro),
                res.getInteger(R.integer.key_code_letter_S_sa1),
                res.getInteger(R.integer.key_code_letter_S_sa2),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_t)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_t_ro),
                res.getInteger(R.integer.key_code_letter_t_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_T)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_T_ro),
                res.getInteger(R.integer.key_code_letter_T_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_d)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_d_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_D)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_D_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )

        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_u)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_u_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_U)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_U_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_h)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_h_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_H)] = arrayListOf(
                res.getInteger(R.integer.key_code_letter_H_sa),
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_e)] = arrayListOf(
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_y)] = arrayListOf(
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )
        extraKeysCodesMap[res.getInteger(R.integer.key_code_letter_o)] = arrayListOf(
                res.getInteger(R.integer.key_code_extra_accent1),
                res.getInteger(R.integer.key_code_extra_accent2)
        )

        extraKeysCodesMap[R.integer.key_code_symbols_punctuation.getVal(context)] = arrayListOf(
                R.integer.key_code_hyphen.getVal(context),
                R.integer.key_code_apostrophy.getVal(context),
                R.integer.key_code_question_mark.getVal(context),
                R.integer.key_code_period.getVal(context),
                R.integer.key_code_comma.getVal(context),
                R.integer.key_code_exclamation_mark.getVal(context),
                R.integer.key_code_quote.getVal(context),
                R.integer.key_code_semicolon.getVal(context),
                R.integer.key_code_colon.getVal(context),
                R.integer.key_code_underscore.getVal(context)
        )

        extraKeysCodesMap[R.integer.key_code_symbols_braces.getVal(context)] = arrayListOf(
                R.integer.key_code_open_paranthesis.getVal(context),
                R.integer.key_code_close_paranthesis.getVal(context),
                R.integer.key_code_open_square_paranthesis.getVal(context),
                R.integer.key_code_close_square_paranthesis.getVal(context),
                R.integer.key_code_open_curly_brace.getVal(context),
                R.integer.key_code_close_curly_brace.getVal(context),
                R.integer.key_code_slash.getVal(context),
                R.integer.key_code_backslash.getVal(context),
                R.integer.key_code_pipe.getVal(context),
                R.integer.key_code_dollar.getVal(context)
        )

        extraKeysCodesMap[R.integer.key_code_symbols.getVal(context)] = arrayListOf(
                R.integer.key_code_plus.getVal(context),
                R.integer.key_code_multiplication.getVal(context),
                R.integer.key_code_division.getVal(context),
                R.integer.key_code_percent.getVal(context),
                R.integer.key_code_at.getVal(context),
                R.integer.key_code_equal.getVal(context),
                R.integer.key_code_lt.getVal(context),
                R.integer.key_code_gt.getVal(context),
                R.integer.key_code_xor.getVal(context),
                R.integer.key_code_tilda.getVal(context)
        )
    }
}
