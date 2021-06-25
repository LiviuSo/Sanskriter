package com.android.lvicto.db

import androidx.room.TypeConverter
import com.android.lvicto.db.data.*

class Converters {

    @TypeConverter
    fun toGramaticalCase(string: String): GrammaticalCase = GrammaticalCase.getValueFromAbbr(string)

    @TypeConverter
    fun fromGramaticalCase(value: GrammaticalCase): String = value.abbr

    @TypeConverter
    fun toGramaticalNumber(string: String): GrammaticalNumber = GrammaticalNumber.getValueFromAbbr(string)

    @TypeConverter
    fun fromGramaticalNumber(value: GrammaticalNumber): String = value.abbr

    @TypeConverter
    fun toGramaticalGender(string: String): GrammaticalGender = GrammaticalGender.getValueFromAbbr(string)

    @TypeConverter
    fun fromGramaticalGender(value: GrammaticalGender) = value.abbr

    @TypeConverter
    fun fromGramaticalType(value: GrammaticalType) = value.denom

    @TypeConverter
    fun toGramaticalType(string: String): GrammaticalType = GrammaticalType.getValueFromDenom(string)

    @TypeConverter
    fun fromVerbClass(clas: VerbClass) = clas.clas

    @TypeConverter
    fun toVerbClass(int: Int) = VerbClass.getValueFromClass(int)
}