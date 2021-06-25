package com.android.lvicto.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conjugation_table")
data class Conjugation(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long,
    @field:ColumnInfo(name = "paradigmRoot") var paradigmRoot: String,
    @field:ColumnInfo(name = "ending") var ending: String,
    @field:ColumnInfo(name = "verbClass") var verbClass: String?,
    @field:ColumnInfo(name = "verbNumber") var verbNumber: String?,
    @field:ColumnInfo(name = "verbPerson") var verbPerson: String?,
    @field:ColumnInfo(name = "verbTime") var verbTime: String?,
    @field:ColumnInfo(name = "verbMode") var verbMode: String?,
    @field:ColumnInfo(name = "verbParadygmType") var verbParadygmType: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    fun clone(): Conjugation = Conjugation(
        this.id,
        this.paradigmRoot,
        this.ending,
        this.verbClass,
        this.verbNumber,
        this.verbPerson,
        this.verbTime,
        this.verbMode,
        this.verbParadygmType
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(paradigmRoot)
        parcel.writeString(ending)
        parcel.writeString(verbClass)
        parcel.writeString(verbNumber)
        parcel.writeString(verbPerson)
        parcel.writeString(verbTime)
        parcel.writeString(verbMode)
        parcel.writeString(verbParadygmType)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String = StringBuffer().apply {
        this.append(paradigmRoot).append(" -").append(ending).append("\n")
            .append("class: ").append(verbClass).append(", ")
            .append(verbPerson).append(", ")
            .append(verbNumber).append(", ")
            .append(verbTime).append("\n")
            .append(verbMode).append(", ")
            .append(verbParadygmType)
    }.toString()

    companion object CREATOR : Parcelable.Creator<Conjugation> {
        override fun createFromParcel(parcel: Parcel): Conjugation {
            return Conjugation(parcel)
        }

        override fun newArray(size: Int): Array<Conjugation?> {
            return arrayOfNulls(size)
        }
    }
}
