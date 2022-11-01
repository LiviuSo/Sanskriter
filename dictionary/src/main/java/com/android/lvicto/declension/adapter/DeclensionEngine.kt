package com.android.lvicto.declension.adapter

import android.content.Context
import com.android.lvicto.common.Word
import com.android.lvicto.db.data.GrammaticalType

class DeclensionEngine(val context: Context) {
    companion object {
        const val PARADIGM_KANTA = "kānta"

    }

    fun getWordDeclensionRoot(word: Word?): String = word?.let {
            if ((it.gType == GrammaticalType.NOUN || it.gType == GrammaticalType.ADJECTIVE || it.gType == GrammaticalType.PROPER_NOUN) && it.paradigm == PARADIGM_KANTA) {
                when {
                    it.wordIAST.endsWith("aḥ") -> it.wordIAST.dropLast(2)
                    it.wordIAST.endsWith("am") -> it.wordIAST.dropLast(2)
                    it.wordIAST.endsWith("ā") -> it.wordIAST.dropLast(1)
                    it.wordIAST.endsWith("a") -> it.wordIAST.dropLast(1)
                    else -> it.wordIAST
                }
            } else { // todo for the other paradigms
                ""
            }
        } ?: ""
}