package com.android.lvicto.sanskriter.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable


@Entity(tableName = "word_table")
data class Word(@field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long = 0,
                @field:ColumnInfo(name = "word") val word: String,
                @field:ColumnInfo(name = "wordIAST") val wordIAST: String,
                @field:ColumnInfo(name = "meaningEn") val meaningEn: String = "",
                @field:ColumnInfo(name = "meaningRo") val meaningRo: String = "") : Parcelable {

    constructor(parcel: Parcel)
            : this(parcel.readLong(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!)

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeLong(id)
        dest.writeString(word)
        dest.writeString(wordIAST)
        dest.writeString(meaningEn)
        dest.writeString(meaningRo)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Word> {
        override fun createFromParcel(parcel: Parcel): Word {
            return Word(parcel)
        }

        override fun newArray(size: Int): Array<Word?> {
            return arrayOfNulls(size)
        }
    }
}