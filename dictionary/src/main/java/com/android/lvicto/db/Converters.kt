package com.android.lvicto.db

import androidx.room.TypeConverter
import com.android.lvicto.data.*

class Converters {

    @TypeConverter
    fun toGramaticalCase(string: String): GramaticalCase = GramaticalCase.getValueFromAbbr(string)

    @TypeConverter
    fun fromGramaticalCase(value: GramaticalCase): String = value.abbr

    @TypeConverter
    fun toGramaticalNumber(string: String): GramaticalNumber = GramaticalNumber.getValueFromAbbr(string)

    @TypeConverter
    fun fromGramaticalNumber(value: GramaticalNumber): String = value.abbr

    @TypeConverter
    fun toGramaticalGender(string: String): GramaticalGender = GramaticalGender.getValueFromAbbr(string)

    @TypeConverter
    fun fromGramaticalGender(value: GramaticalGender) = value.abbr

    @TypeConverter
    fun fromGramaticalType(value: GramaticalType) = value.denom

    @TypeConverter
    fun toGramaticalType(string: String): GramaticalType = GramaticalType.getValueFromDenom(string)

    @TypeConverter
    fun fromVerbClass(clas: VerbClass) = clas.clas

    @TypeConverter
    fun toVerbClass(int: Int) = VerbClass.getValueFromClass(int)
}