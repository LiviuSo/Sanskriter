package com.android.lvicto.data

enum class GrammaticalGender(val abbr: String) {
    NONE("n/a"),
    MASCULIN("M."),
    FEMININ("F."),
    NEUTER("N.");

    companion object {
        fun getValueFromAbbr(abbr: String): GrammaticalGender {
            return when (abbr) {
                MASCULIN.abbr -> MASCULIN
                FEMININ.abbr -> FEMININ
                NEUTER.abbr -> NEUTER
                else -> NONE
            }
        }

        fun getPosition(gender: GrammaticalGender?): Int {
            return gender?.let {
                when (it) {
                    MASCULIN -> 0
                    FEMININ -> 1
                    NEUTER -> 2
                    NONE -> 3
                }
            } ?: 3
        }
    }
}

enum class GrammaticalNumber(val abbr: String) {
    NONE("n/a."),
    SINGULAR("sg."),
    DUAL("du."),
    PLURAL("pl.");

    companion object {
        fun getValueFromAbbr(abbr: String): GrammaticalNumber {
            return when (abbr) {
                SINGULAR.abbr -> SINGULAR
                DUAL.abbr -> DUAL
                PLURAL.abbr -> PLURAL
                else -> NONE
            }
        }
    }
}

enum class GrammaticalCase(val abbr: String) {
    NONE("n/a."),
    NOMINATIV("Nom."),
    ACCUSATIV("Acc."),
    INSTRUMENTAL("Ins."),
    DATIV("Dat."),
    ABLATIV("Abl."),
    GENITIV("Gen."),
    LOCATIV("Loc."),
    VOCATIV("Voc.");

    companion object {
        fun getValueFromAbbr(abbr: String): GrammaticalCase {
            return when (abbr) {
                NOMINATIV.abbr -> NOMINATIV
                ACCUSATIV.abbr -> ACCUSATIV
                INSTRUMENTAL.abbr -> INSTRUMENTAL
                DATIV.abbr -> DATIV
                ABLATIV.abbr -> ABLATIV
                GENITIV.abbr -> GENITIV
                LOCATIV.abbr -> LOCATIV
                VOCATIV.abbr -> VOCATIV
                else -> NONE
            }
        }
    }
}

enum class GrammaticalType(val denom: String) {
    NOUN("noun"),
    PROPER_NOUN("proper noun"),
    ADJECTIVE("adjective"),
    ADVERB("adverb"),
    PRONOUN("pronoun"),
    VERB("verb"),
    INTERJECTION("interjection"),
    PREPOSITION("preposition"),
    SUFFIX("suffix"),
    PREFIX("prefix"),
    OTHER("other");

    companion object {
        fun getValueFromDenom(denomination: String): GrammaticalType {
            return when (denomination) {
                NOUN.denom -> NOUN
                PROPER_NOUN.denom -> PROPER_NOUN
                ADJECTIVE.denom -> ADJECTIVE
                ADVERB.denom -> ADVERB
                PRONOUN.denom -> PRONOUN
                VERB.denom -> VERB
                INTERJECTION.denom -> INTERJECTION
                PREPOSITION.denom -> PREPOSITION
                SUFFIX.denom -> SUFFIX
                PREFIX.denom -> PREFIX
                else -> OTHER
            }
        }

        fun getPosition(type: GrammaticalType?): Int {
            return type?.let {
                when (it) {
                    NOUN -> 0
                    PROPER_NOUN -> 1
                    ADJECTIVE -> 2
                    ADVERB -> 3
                    PRONOUN -> 4
                    VERB -> 5
                    INTERJECTION -> 6
                    PREPOSITION -> 7
                    SUFFIX -> 8
                    PREFIX -> 9
                    else -> 10
                }
            } ?: 10
        }
    }
}

enum class VerbClass(val clas: Int) {
    NONE(0),
    I(1),
    II(2),
    III(3),
    IV(4),
    V(5),
    VI(6),
    VII(7),
    VIII(8),
    IX(9),
    X(10);

    companion object {
        fun getValueFromClass(cl: Int): VerbClass {
            return when (cl) {
                I.clas -> I
                II.clas -> II
                III.clas -> III
                IV.clas -> IV
                V.clas -> V
                VI.clas -> VI
                VII.clas -> VII
                VIII.clas -> VIII
                IX.clas -> IX
                X.clas -> X
                else -> NONE
            }
        }

        fun getPosition(verbClass: VerbClass?): Int = verbClass?.let {
            when (verbClass) {
                I -> 1
                II -> 2
                III -> 3
                IV -> 4
                V -> 5
                VI -> 6
                VII -> 7
                VIII -> 8
                IX -> 9
                X -> 10
                else -> 0
            }
        } ?: 0

        fun toVerbClassFromName(name: String): VerbClass = when (name) {
            "I" -> I
            "II" -> II
            "III" -> III
            "IV" -> IV
            "V" -> V
            "VI" -> VI
            "VII" -> VII
            "VIII" -> VIII
            "IX" -> IX
            "X" -> X
            else -> NONE
        }
    }
}