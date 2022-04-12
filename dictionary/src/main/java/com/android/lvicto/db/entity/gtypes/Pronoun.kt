package com.android.lvicto.db.entity.gtypes

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.lvicto.common.Constants.TABLE_WORDS_PRONOUNS
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.*
import com.android.lvicto.db.entity.Word


@Entity(tableName = TABLE_WORDS_PRONOUNS)
data class Pronoun(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long = 0,
    @field:ColumnInfo(name = "gType") var gType: GrammaticalType = GrammaticalType.OTHER,
    @field:ColumnInfo(name = "word") var word: String,
    @field:ColumnInfo(name = "wordIAST") var wordIAST: String,
    @field:ColumnInfo(name = "meaningEn") var meaningEn: String = "",
    @field:ColumnInfo(name = "meaningRo") var meaningRo: String = "",
    @field:ColumnInfo(name = "paradigm") var paradigm: String = "",
    @field:ColumnInfo(name = "gender") var gender: GrammaticalGender = GrammaticalGender.NONE,
    @field:ColumnInfo(name = "number") var number: GrammaticalNumber = GrammaticalNumber.NONE,
    @field:ColumnInfo(name = "person") var person: GrammaticalPerson = GrammaticalPerson.NONE,
    @field:ColumnInfo(name = "gCase") var gCase: GrammaticalCase = GrammaticalCase.NONE
) : Parcelable {

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
        converters.toGrammaticalCase(parcel.readString()!!)
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeLong(id)
        dest.writeString(converters.fromGrammaticalType(gType))
        dest.writeString(word)
        dest.writeString(wordIAST)
        dest.writeString(meaningEn)
        dest.writeString(meaningRo)
        dest.writeString(paradigm)
        dest.writeString(converters.fromGrammaticalGender(gender))
        dest.writeString(converters.fromGrammaticalNumber(number))
        dest.writeString(converters.fromGrammaticalPerson(person))
        dest.writeString(converters.fromGrammaticalCase(gCase))
    }

    override fun describeContents(): Int = 0

    override fun toString(): String = StringBuffer().also {
        val na = "n/a"
        it.append("id: $id \n") // for debug
        it.append("type: $gType \n")
        it.append("word (Sa): ${word.ifEmpty { na }} \n")
        it.append("word (IAST): ${wordIAST.ifEmpty { na }} \n")
        it.append("meaning (En): ${meaningEn.ifEmpty { na }} \n")
        it.append("meaning (Ro): ${meaningRo.ifEmpty { na }} \n")
        it.append("paradigm: ${paradigm.ifEmpty { na }} \n")
        it.append("gender: ${gender.abbr} \n")
        it.append("number: ${number.abbr.ifEmpty { na }} \n")
        it.append("person: ${person.abbr.ifEmpty { na }} \n")
        it.append("case: ${gCase.abbr.ifEmpty { na }} \n")
    }.toString()

    companion object CREATOR : Parcelable.Creator<Word> {
        @Ignore
        val converters = Converters()

        override fun createFromParcel(parcel: Parcel): Word {
            return Word(parcel)
        }

        override fun newArray(size: Int): Array<Word?> {
            return arrayOfNulls(size)
        }
    }

}