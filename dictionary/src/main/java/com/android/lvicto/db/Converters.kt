package com.android.lvicto.db

import androidx.room.TypeConverter
import com.android.lvicto.db.data.*

class Converters {

    @TypeConverter
    fun toGrammaticalCase(string: String): GrammaticalCase = GrammaticalCase.getValueFromAbbr(string)

    @TypeConverter
    fun fromGrammaticalCase(value: GrammaticalCase): String = value.abbr

    @TypeConverter
    fun toGrammaticalNumber(string: String): GrammaticalNumber = GrammaticalNumber.getValueFromAbbr(string)

    @TypeConverter
    fun fromGrammaticalNumber(value: GrammaticalNumber): String = value.abbr

    @TypeConverter
    fun toGrammaticalGender(string: String): GrammaticalGender = GrammaticalGender.getValueFromAbbr(string)

    @TypeConverter
    fun fromGrammaticalGender(value: GrammaticalGender) = value.abbr

    @TypeConverter
    fun fromGrammaticalType(value: GrammaticalType) = value.denom

    @TypeConverter
    fun toGrammaticalType(string: String): GrammaticalType = GrammaticalType.getValueFromDenom(string)

    @TypeConverter
    fun fromVerbClass(clas: VerbClass) = clas.clas

    @TypeConverter
    fun toVerbClass(int: Int) = VerbClass.getValueFromClass(int)

    @TypeConverter
    fun fromGrammaticalPerson(person: GrammaticalPerson) = person.abbr

    @TypeConverter
    fun toGrammaticalPerson(value: String) = GrammaticalPerson.getValueFromAbbr(value)
}