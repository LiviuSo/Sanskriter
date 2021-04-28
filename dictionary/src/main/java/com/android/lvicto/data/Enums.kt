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
        fun getValueFromAbbr(abbr: String) : GrammaticalCase {
            return  when(abbr) {
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
    ADJECTIVE("adjective"),
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
                ADJECTIVE.denom -> ADJECTIVE
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
                    ADJECTIVE -> 1
                    PRONOUN -> 2
                    VERB -> 3
                    INTERJECTION -> 4
                    PREPOSITION -> 5
                    SUFFIX -> 6
                    PREFIX -> 7
                    else -> 8
                }
            } ?: 8
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
                I -> 0
                II -> 1
                III -> 2
                IV -> 3
                V -> 4
                VI -> 5
                VII -> 6
                VIII -> 7
                IX -> 8
                X -> 9
                else -> 10
            }
        } ?: 10
    }
}