package com.android.lvicto.declension

import android.app.Application
import android.util.Log
import com.android.lvicto.db.GrammarDatabase
import com.android.lvicto.db.data.GrammaticalCase
import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.data.GrammaticalNumber
import com.android.lvicto.db.entity.Declension

class DeclensionRepositoryImpl internal constructor(val application: Application) :
    DeclensionRepository {

    private val declensionDao = GrammarDatabase.getInstance(application).declensionDao()

    override suspend fun getAll(): List<Declension> = declensionDao.getAll()

    override suspend fun insert(declension: Declension) {
        declensionDao.insert(declension)
    }

    override suspend fun delete(declension: Declension): Int =
        declensionDao.deleteDeclension(arrayListOf(declension))

    override suspend fun filter(declension: Declension): List<Declension> {
        createLog(declension)
        return declensionDao.getDeclensions(
            if(declension.paradigm.isNotEmpty()) {
                "%${declension.paradigm}%"
            } else {
                "%%"
            }
            , if(declension.paradigmEnding.isNotEmpty()) {
                "%${declension.paradigmEnding}"
            } else {
                "%"
            }
            , if(declension.suffix.isNotEmpty()) {
                "%${declension.suffix}%"
            } else {
                "%"
            }
            , if (declension.gCase.abbr != GrammaticalCase.NONE.abbr) {
                declension.gCase.abbr
            } else {
                "%%"
            }
            , if (declension.gNumber.abbr != GrammaticalNumber.NONE.abbr) {
                declension.gNumber.abbr
            } else {
                "%%"
            }
            , if (declension.gGender.abbr != GrammaticalGender.NONE.abbr) {
                declension.gGender.abbr
            } else {
                "%%"
            }
        )
    }

    override suspend fun filterByFullSuffix(suffix: String): List<Declension> =
        declensionDao.getDeclension(suffix)

    override suspend fun getSuffixes(part: String): List<String> = declensionDao.getSuffixes(part)

    // debug only
    private fun createLog(declension: Declension) {
        val stringBuffer = StringBuffer()
        stringBuffer.append(
            if (declension.paradigm.isNotEmpty()) {
                declension.paradigm
            } else {
                "[no paradigm]"
            }
        )
        stringBuffer.append(" ")
        stringBuffer.append(
            if (declension.paradigmEnding.isNotEmpty()) {
                declension.paradigmEnding
            } else {
                "[no paradigm ending]"
            }
        )
        stringBuffer.append(" ")
        stringBuffer.append(
            if (declension.suffix.isNotEmpty()) {
                declension.suffix
            } else {
                "[no suffix]"
            }
        )
        stringBuffer.append(" ")
        stringBuffer.append(
            if (declension.gCase.abbr == GrammaticalCase.NONE.abbr) {
                declension.gCase.abbr
            } else {
                "[no case]"
            }
        )
        stringBuffer.append(" ")
        stringBuffer.append(
            if (declension.gNumber.abbr == GrammaticalNumber.NONE.abbr) {
                declension.gNumber.abbr
            } else {
                "[no number]"
            }
        )
        stringBuffer.append(" ")
        stringBuffer.append(
            if (declension.gGender.abbr == GrammaticalGender.NONE.abbr) {
                declension.gGender.abbr
            } else {
                "[no gender]"
            }
        )
        Log.d("Ramachandra", stringBuffer.toString())
    }
}