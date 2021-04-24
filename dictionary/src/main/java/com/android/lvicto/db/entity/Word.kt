package com.android.lvicto.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.lvicto.data.GramaticalType
import com.android.lvicto.data.VerbClass
import com.android.lvicto.db.Converters


@Entity(tableName = "word_table")
data class Word(@field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long = 0,
                @field:ColumnInfo(name = "word") val word: String,
                @field:ColumnInfo(name = "wordIAST") val wordIAST: String,
                @field:ColumnInfo(name = "meaningEn") val meaningEn: String = "",
                @field:ColumnInfo(name = "meaningRo") val meaningRo: String = "",
                @field:ColumnInfo(name = "gType") val gType: GramaticalType = GramaticalType.OTHER,
                @field:ColumnInfo(name = "paradigm") val paradigm: String = "",
                @field:ColumnInfo(name = "verbClass") val verbClass: VerbClass = VerbClass.NONE
                ) : Parcelable {

    constructor(parcel: Parcel)
            : this(parcel.readLong(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                converters.toGramaticalType(parcel.readString()!!),
                parcel.readString()!!,
                converters.toVerbClass(parcel.readInt()))

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeLong(id)
        dest.writeString(word)
        dest.writeString(wordIAST)
        dest.writeString(meaningEn)
        dest.writeString(meaningRo)
        dest.writeString(converters.fromGramaticalType(gType))
        dest.writeString(paradigm)
        dest.writeInt(converters.fromVerbClass(verbClass))
    }

    override fun describeContents(): Int = 0

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