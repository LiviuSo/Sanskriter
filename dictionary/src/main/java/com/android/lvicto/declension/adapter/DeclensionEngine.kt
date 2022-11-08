package com.android.lvicto.declension.adapter

import android.content.Context
import com.android.lvicto.common.Word
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.db.data.Paradigm

class DeclensionEngine(val context: Context) {
    companion object {
        val PARADIGM_KANTA = Paradigm.KANTA.paradigm
        val PARADIGM_NADI = Paradigm.NADI.paradigm
    }

    fun getWordDeclensionRoot(word: Word?): String = word?.let {
            if (it.gType == GrammaticalType.NOUN || it.gType == GrammaticalType.ADJECTIVE || it.gType == GrammaticalType.PROPER_NOUN) {
                if(it.paradigm == PARADIGM_KANTA) {
                    when {
                        it.wordIAST.endsWith("aḥ") -> it.wordIAST.dropLast(2)
                        it.wordIAST.endsWith("am") -> it.wordIAST.dropLast(2)
                        it.wordIAST.endsWith("ā") -> it.wordIAST.dropLast(1)
                        it.wordIAST.endsWith("a") -> it.wordIAST.dropLast(1)
                        else -> it.wordIAST
                    }
                } else if(it.paradigm == PARADIGM_NADI) {
                    if(it.wordIAST.endsWith("ī")) {
                        it.wordIAST.dropLast(1)
                    } else {
                        ""
                    }
                } else {  // todo for the other paradigms
                    ""
                }
            } else {
                ""
            }
        } ?: ""
}