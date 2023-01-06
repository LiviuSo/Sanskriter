package com.android.lvicto.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.lvicto.common.Constants.EMPTY_STRING
import com.android.lvicto.db.Converters
import com.android.lvicto.db.data.GrammaticalCase
import com.android.lvicto.db.data.GrammaticalGender
import com.android.lvicto.db.data.GrammaticalNumber
import java.util.*

@Entity(tableName = "declension_table")
data class Declension(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") var id: Long = 0,
    @field:ColumnInfo(name = "gCase") val gCase: GrammaticalCase = GrammaticalCase.NONE,
    @field:ColumnInfo(name = "gNumber") val gNumber: GrammaticalNumber = GrammaticalNumber.NONE,
    @field:ColumnInfo(name = "gGender") var gGender: GrammaticalGender = GrammaticalGender.NONE,
    @field:ColumnInfo(name = "paradigm") var paradigm: String = EMPTY_STRING,                      // eg, kanta
    @field:ColumnInfo(name = "paradigmEnding") val paradigmEnding: String = EMPTY_STRING,          // eg, -a
    @field:ColumnInfo(name = "suffix") var suffix: String = EMPTY_STRING,                          // eg, -asya
    @field:ColumnInfo(name = "paradigmDeclension") var paradigmDeclension: String = EMPTY_STRING   // eg, kantasya
) : Parcelable {

    override fun toString(): String = StringBuffer()
        .append(paradigm)
        .append(" (").append(paradigmEnding).append("), ")
        .append(gNumber.abbr.lowercase(Locale.ROOT)).append(", ")
        .append(gGender.abbr.uppercase(Locale.ROOT)).append(", ")
        .append(gCase.abbr[0]).append(gCase.abbr.substring(1).lowercase(Locale.ROOT)).append(", ")
        .append(if(suffix.isEmpty()) "-" else "-$suffix").append(", ")
        .append(paradigmDeclension)
        .toString()

    fun toString(wordDeclensionRoot: String): String = StringBuffer()
        .append(this.gNumber.abbr.lowercase(Locale.ROOT)).append(", ")
        .append(this.gGender.abbr.uppercase(Locale.ROOT)).append(", ")
        .append(this.gCase.abbr[0]).append(this.gCase.abbr.substring(1).lowercase(Locale.ROOT))
        .append(", ")
        .append(createDeclension(wordDeclensionRoot = wordDeclensionRoot, declensionSuffix = suffix))
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

    companion object CREATOR : Parcelable.Creator<Declension> {
        @Ignore
        val converters = Converters()

        override fun createFromParcel(parcel: Parcel): Declension = Declension(parcel)

        override fun newArray(size: Int): Array<Declension?> = arrayOfNulls(size)

        fun createDeclension(wordDeclensionRoot: String, declensionSuffix: String): String =
            "$wordDeclensionRoot$declensionSuffix"
    }

}