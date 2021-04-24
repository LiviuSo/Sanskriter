package com.android.lvicto.data

enum class GramaticalGender(val abbr: String) {
    NONE("n/a"),
    MASCULIN("M."),
    FEMININ("F."),
    NEUTER("N.");

    companion object {
        fun getValueFromAbbr(abbr: String) : GramaticalGender {
            return  when(abbr) {
                MASCULIN.abbr -> MASCULIN
                FEMININ.abbr -> FEMININ
                NEUTER.abbr -> NEUTER
                else -> NONE
            }
        }
    }
}

enum class GramaticalNumber(val abbr: String) {
    NONE("n/a."),
    SINGULAR("sg."),
    DUAL("du."),
    PLURAL("pl.");

    companion object {
        fun getValueFromAbbr(abbr: String) : GramaticalNumber {
            return  when(abbr) {
                SINGULAR.abbr -> SINGULAR
                DUAL.abbr -> DUAL
                PLURAL.abbr -> PLURAL
                else -> NONE
            }
        }
    }
}

enum class GramaticalCase(val abbr: String) {
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
        fun getValueFromAbbr(abbr: String) : GramaticalCase {
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

enum class GramaticalType(val denom: String) {
    NOUN("noun"),
    ADJECTIVE("adjective"),
    PRONOUN("pronoun"),
    VERB("verb"),
    INTERJECTION("interjection"),
    PREPOSITION("PREPOSITION"),
    SUFFIX("suffix"),
    PREFIX("prefix"),
    OTHER("other");

    companion object {
        fun getValueFromDenom(denomination: String): GramaticalType {
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
    }
}

enum class VerbClass(val clas: Int) {
    NONE(0),
    I(1),
    II(1),
    III(1),
    IV(1),
    V(1),
    VI(1),
    VII(1),
    VIII(1),
    IX(1),
    X(1);

    companion object {
        fun getValueFromClass(cl: Int) : VerbClass {
            return when(cl) {
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
    }
}