package com.android.lvicto.common.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.lvicto.common.db.data.GrammaticalGender
import com.android.lvicto.common.db.data.GrammaticalType
import com.android.lvicto.common.db.data.VerbClass
import com.android.lvicto.common.db.Converters


@Entity(tableName = "word_table")
data class Word(@field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long = 0,
                @field:ColumnInfo(name = "word") var word: String,
                @field:ColumnInfo(name = "wordIAST") var wordIAST: String,
                @field:ColumnInfo(name = "meaningEn") var meaningEn: String = "",
                @field:ColumnInfo(name = "meaningRo") var meaningRo: String = "",
                @field:ColumnInfo(name = "gType") var gType: GrammaticalType = GrammaticalType.OTHER,
                @field:ColumnInfo(name = "paradigm") var paradigm: String = "",
                @field:ColumnInfo(name = "verbClass") var verbClass: VerbClass = VerbClass.NONE,
                @field:ColumnInfo(name = "gender") var gender: GrammaticalGender = GrammaticalGender.NONE
                ) : Parcelable {

    constructor(parcel: Parcel)
            : this(parcel.readLong(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                converters.toGramaticalType(parcel.readString()!!),
                parcel.readString()!!,
                converters.toVerbClass(parcel.readInt()),
                converters.toGramaticalGender(parcel.readString()!!)
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeLong(id)
        dest.writeString(word)
        dest.writeString(wordIAST)
        dest.writeString(meaningEn)
        dest.writeString(meaningRo)
        dest.writeString(converters.fromGramaticalType(gType))
        dest.writeString(paradigm)
        dest.writeInt(converters.fromVerbClass(verbClass))
        dest.writeString(converters.fromGramaticalGender(gender))
    }

    override fun describeContents(): Int = 0

    override fun toString(): String = StringBuffer().also {
        val na = "n/a"
        it.append("id: $id \n") // for debug
        it.append("word (Sa): ${if(word.isNotEmpty()) { word } else { na }} \n")
        it.append("word (IAST): ${if(wordIAST.isNotEmpty()) { wordIAST } else { na }} \n")
        it.append("meaning (En): ${if(meaningEn.isNotEmpty()) { meaningEn } else { na }} \n")
        it.append("meaning (Ro): ${if(meaningRo.isNotEmpty()) { meaningRo } else { na }} \n")
        it.append("type: $gType \n")
        if(gType == GrammaticalType.PROPER_NOUN) {
            it.append("paradigm: ${if(paradigm.isNotEmpty()) { paradigm } else { na }} \n")
        }
        if(gType == GrammaticalType.NOUN || gType == GrammaticalType.ADJECTIVE) {
            it.append("gender: ${gender.abbr} \n")
            it.append("paradigm: ${if(paradigm.isNotEmpty()) { paradigm } else { na }} \n")
        }
        if(gType == GrammaticalType.VERB) {
            it.append("class (verb): $verbClass\n")
        }
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