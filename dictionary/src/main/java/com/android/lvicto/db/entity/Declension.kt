package com.android.lvicto.db.entity

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.lvicto.db.Converters
import com.android.lvicto.db.GramaticalCase
import com.android.lvicto.db.GramaticalGender
import com.android.lvicto.db.GramaticalNumber
import com.android.lvicto.db.entity.Declension.CREATOR.converters
import java.util.*

@Entity(tableName = "declension_table")
data class Declension(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long = 0,
    @field:ColumnInfo(name = "gCase") val gCase: GramaticalCase,
    @field:ColumnInfo(name = "gNumber") val gNumber: GramaticalNumber,
    @field:ColumnInfo(name = "gGender") val gGender: GramaticalGender,
    @field:ColumnInfo(name = "paradigm") val paradigm: String,                      // eg, kanta
    @field:ColumnInfo(name = "paradigmEnding") val paradigmEnding: String,          // eg, -a
    @field:ColumnInfo(name = "suffix") val suffix: String,                          // eg, -asya
    @field:ColumnInfo(name = "paradigmDeclension") var paradigmDeclension: String   // eg, kantasya
) : Parcelable {

    override fun toString(): String {
        return StringBuffer()
            .append(this.gCase.abbr[0]).append(this.gCase.abbr.substring(1).toLowerCase(Locale.ROOT)).append(", ")
            .append(this.gNumber.abbr.toLowerCase(Locale.ROOT)).append(", ")
            .append(this.gGender.abbr.toUpperCase(Locale.ROOT)).append(", ")
            .append(this.paradigm).append(" (-").append(this.paradigmEnding).append("), ")
            .append(this.paradigmDeclension)
            .toString()
    }

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        converters.toGramaticalCase(parcel.readString().toString()),
        converters.toGramaticalNumber(parcel.readString().toString()),
        converters.toGramaticalGender(parcel.readString().toString()),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(converters.fromGramaticalCase(gCase))
        parcel.writeString(converters.fromGramaticalNumber(gNumber))
        parcel.writeString(converters.fromGramaticalGender(gGender))
        parcel.writeString(paradigm)
        parcel.writeString(paradigmEnding)
        parcel.writeString(suffix)
        parcel.writeString(paradigmDeclension)
    }


    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Declension> {
        @Ignore
        val converters = Converters()

        override fun createFromParcel(parcel: Parcel): Declension = Declension(parcel)

        override fun newArray(size: Int): Array<Declension?> {
            return arrayOfNulls(size)
        }

        fun createDeclension(paradigm: String, paradigmEnding: String, declensionSuffix: String): String {
            // generate the paradigm declension
            return if(paradigm.length > paradigmEnding.length && paradigm.contains(paradigmEnding)) {
                val paradigmRoot = paradigm.dropLast(paradigmEnding.length)
                "$paradigmRoot$declensionSuffix"
            } else {
                ""
            }
        }
    }

}