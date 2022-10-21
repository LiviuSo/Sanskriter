package com.android.lvicto.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.GrammaticalCase
import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.data.GrammaticalNumber
import java.util.*

@Entity(tableName = "declension_table")
data class Declension(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long = 0,
    @field:ColumnInfo(name = "gCase") val gCase: GrammaticalCase,
    @field:ColumnInfo(name = "gNumber") val gNumber: GrammaticalNumber,
    @field:ColumnInfo(name = "gGender") val gGender: GrammaticalGender,
    @field:ColumnInfo(name = "paradigm") val paradigm: String,                      // eg, kanta
    @field:ColumnInfo(name = "paradigmEnding") val paradigmEnding: String,          // eg, -a
    @field:ColumnInfo(name = "suffix") val suffix: String,                          // eg, -asya
    @field:ColumnInfo(name = "paradigmDeclension") var paradigmDeclension: String   // eg, kantasya
) : Parcelable {

    override fun toString(): String = StringBuffer()
        .append(this.gCase.abbr[0]).append(this.gCase.abbr.substring(1).lowercase(Locale.ROOT))
        .append(", ")
        .append(this.gNumber.abbr.lowercase(Locale.ROOT)).append(", ")
        .append(this.gGender.abbr.uppercase(Locale.ROOT)).append(", ")
        .append(this.paradigm).append(" (-").append(this.paradigmEnding).append("), ")
        .append("${this.suffix}, ")
        .append(this.paradigmDeclension)
        .toString()

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        converters.toGrammaticalCase(parcel.readString().toString()),
        converters.toGrammaticalNumber(parcel.readString().toString()),
        converters.toGrammaticalGender(parcel.readString().toString()),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(converters.fromGrammaticalCase(gCase))
        parcel.writeString(converters.fromGrammaticalNumber(gNumber))
        parcel.writeString(converters.fromGrammaticalGender(gGender))
        parcel.writeString(paradigm)
        parcel.writeString(paradigmEnding)
        parcel.writeString(suffix)
        parcel.writeString(paradigmDeclension)
    }

    override fun describeContents(): Int = 0

    fun toString(wordDeclensionRoot: String): String = StringBuffer()
        .append(this.gCase.abbr[0]).append(this.gCase.abbr.substring(1).lowercase(Locale.ROOT))
        .append(", ")
        .append(this.gNumber.abbr.lowercase(Locale.ROOT)).append(", ")
        .append(this.gGender.abbr.uppercase(Locale.ROOT)).append(", ")
        .append(
            createDeclension(
                wordDeclensionRoot = wordDeclensionRoot,
                declensionSuffix = suffix
            )
        )
        .toString()

    companion object CREATOR : Parcelable.Creator<Declension> {
        @Ignore
        val converters = Converters()

        override fun createFromParcel(parcel: Parcel): Declension = Declension(parcel)

        override fun newArray(size: Int): Array<Declension?> = arrayOfNulls(size)

        fun createDeclension(wordDeclensionRoot: String, declensionSuffix: String): String =
            "$wordDeclensionRoot$declensionSuffix"
    }

}