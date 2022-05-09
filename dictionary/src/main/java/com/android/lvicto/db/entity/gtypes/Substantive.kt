package com.android.lvicto.db.entity.gtypes

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.lvicto.common.Constants.TABLE_WORDS_SUBSTANTIVES
import com.android.lvicto.common.Word
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.*


@Entity(tableName = TABLE_WORDS_SUBSTANTIVES)
data class Substantive(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long = 0,
    @field:ColumnInfo(name = "gType") var gType: GrammaticalType = GrammaticalType.OTHER,
    @field:ColumnInfo(name = "word") var word: String,
    @field:ColumnInfo(name = "wordIAST") var wordIAST: String,
    @field:ColumnInfo(name = "meaningEn") var meaningEn: String = "",
    @field:ColumnInfo(name = "meaningRo") var meaningRo: String = "",
    @field:ColumnInfo(name = "paradigm") var paradigm: String = "",
    @field:ColumnInfo(name = "gender") var gender: GrammaticalGender = GrammaticalGender.NONE
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        converters.toGrammaticalType(parcel.readString()!!),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        converters.toGrammaticalGender(parcel.readString()!!)
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
        if (gType == GrammaticalType.PROPER_NOUN) {
            it.append("paradigm: ${paradigm.ifEmpty { na }} \n")
        }
        if (gType == GrammaticalType.NOUN || gType == GrammaticalType.ADJECTIVE) {
            it.append("gender: ${gender.abbr} \n")
            it.append("paradigm: ${paradigm.ifEmpty { na }} \n")
        }
    }.toString()

    fun wrap() = Word(id = id,
        gType = gType,
        wordSa = word,
        wordIAST = wordIAST,
        meaningEn = meaningEn,
        meaningRo = meaningRo,
        paradigm = paradigm,
        gender = gender,
        number = GrammaticalNumber.NONE,
        person = GrammaticalPerson.NONE,
        grammaticalCase = GrammaticalCase.NONE,
        verbClass = VerbClass.NONE)

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