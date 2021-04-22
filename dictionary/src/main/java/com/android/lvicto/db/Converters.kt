package com.android.lvicto.db

import androidx.room.TypeConverter

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
}