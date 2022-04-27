package com.android.lvicto.common

import android.os.Parcel
import android.os.Parcelable
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.*
import com.android.lvicto.db.entity.Word
import com.android.lvicto.db.entity.gtypes.*

data class WordWrapper(
    var id: Long = 0,
    val gType: GrammaticalType,
    val wordSa: String,
    val wordIAST: String,
    val meaningEn: String,
    val meaningRo: String,
    val paradigm: String = "",
    val gender: GrammaticalGender = GrammaticalGender.NONE,
    val number: GrammaticalNumber = GrammaticalNumber.NONE,
    val person: GrammaticalPerson = GrammaticalPerson.NONE,
    val grammaticalCase: GrammaticalCase = GrammaticalCase.NONE,
    val verbClass: VerbClass = VerbClass.NONE) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        converters.toGrammaticalType(parcel.readString()!!),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        converters.toGrammaticalGender(parcel.readString()!!),
        converters.toGrammaticalNumber(parcel.readString()!!),
        converters.toGrammaticalPerson(parcel.readString()!!),
        converters.toGrammaticalCase(parcel.readString()!!),
        converters.toVerbClass(parcel.readInt())
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeLong(id)
        dest.writeString(converters.fromGrammaticalType(gType))
        dest.writeString(wordSa)
        dest.writeString(wordIAST)
        dest.writeString(meaningEn)
        dest.writeString(meaningRo)
        dest.writeString(converters.fromGrammaticalGender(gender))
        dest.writeString(converters.fromGrammaticalNumber(number))
        dest.writeString(converters.fromGrammaticalPerson(person))
        dest.writeString(converters.fromGrammaticalCase(grammaticalCase))
        dest.writeInt(converters.fromVerbClass(verbClass))
    }

    companion object CREATOR : Parcelable.Creator<WordWrapper> {
        val converters = Converters()

        override fun createFromParcel(parcel: Parcel): WordWrapper = WordWrapper(parcel)

        override fun newArray(size: Int): Array<WordWrapper?> = arrayOfNulls(size)
    }

    fun toNumeral(): Numeral = Numeral(id = id, gType = gType, word = wordSa, wordIAST = wordIAST, meaningEn = meaningEn, meaningRo = meaningRo, gender = gender, gCase = grammaticalCase)

    fun toVerb(): Verb = Verb(id = id, gType = gType, word = wordSa, wordIAST = wordIAST, meaningEn = meaningEn, meaningRo = meaningRo, paradigm = paradigm, verbClass = verbClass)

    fun toPronoun() = Pronoun(id = id, gType = gType, word = wordSa, wordIAST = wordIAST, meaningEn = meaningEn, meaningRo = meaningRo, paradigm = paradigm, gender = gender, number = number, person = person, gCase = grammaticalCase)

    fun toSubstantive() = Substantive(id = id, gType = gType, word = wordSa, wordIAST = wordIAST, meaningEn = meaningEn, meaningRo = meaningRo, paradigm = paradigm, gender = gender)

    fun toOther() = Other(id = id, gType = gType, word = wordSa, wordIAST = wordIAST, meaningEn = meaningEn, meaningRo = meaningRo)

    fun toWord() = Word(id = id, gType = gType, word = wordSa, wordIAST = wordIAST, meaningEn = meaningEn, meaningRo = meaningRo, paradigm = paradigm, verbClass = verbClass, gender = gender)
}