package com.android.lvicto.db.data

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

enum class GrammaticalPerson(val abbr: String) {
    NONE("n/a."),
    FIRST("1st"),
    SECOND("2nd"),
    THIRD("3rd");

    companion object {
        fun getValueFromAbbr(abbr: String): GrammaticalPerson {
            return when (abbr) {
                FIRST.abbr -> FIRST
                SECOND.abbr -> SECOND
                THIRD.abbr -> THIRD
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
    NUMERAL_CARDINAL("numeral cardinal"),
    NUMERAL_ORDINAL("numeral ordinal"),
    OTHER("other");

    companion object {
        fun getValueFromDenom(denomination: String): GrammaticalType = when (denomination) {
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
            NUMERAL_CARDINAL.denom -> NUMERAL_CARDINAL
            NUMERAL_ORDINAL.denom -> NUMERAL_ORDINAL
            else -> OTHER
        }

        fun getPosition(type: GrammaticalType?): Int {
            return type?.let {
                when (it) {
                    // substantive group
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
                    NUMERAL_CARDINAL -> 10
                    NUMERAL_ORDINAL -> 11
                    else -> 12
                }
            } ?: 12
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

enum class Paradigm(val description: String, val paradigm: String) {
    KANTA("stem in a/ā: kānta", "kānta"),
    NADI("stem in ī: nadī", "nadī"),
    STRI("stem in ī: strī (irregular)", "strī"),
    DHI("stem in ī: dhī", "dhī"),
    VADHU("stem in ū: vadhū", "vadhū"),
    BHU("stem in ū: bhū", "bhū"),
    SUCI("stem in i: śuci", "śuci"),
    MRDU("stem in u: mṛdu", "mṛdu"),
    KARTR("stem in ṛ: kartṛ", "kartṛ"),
    PITR("stem in ṛ: pitṛ", "pitṛ"),
    SVASR("stem in ṛ: svasṛ", "svasṛ"),
    MATR("stem in ṛ: mātṛ", "mātṛ"),
    PARADIGM("paradigm", "paradigm");
    // todo complete

    companion object {
        fun fromDescription(descr: String): String = when(descr) {
            KANTA.description -> KANTA
            NADI.description -> NADI
            STRI.description -> STRI
            DHI.description -> DHI
            VADHU.description -> VADHU
            BHU.description -> BHU
            SUCI.description -> SUCI
            MRDU.description -> MRDU
            KARTR.description -> KARTR
            PITR.description -> PITR
            SVASR.description -> SVASR
            MATR.description -> MATR
            else -> PARADIGM
        }.paradigm

        fun getPosition(paradigm: String): Int {
            return when(paradigm) {
                KANTA.paradigm -> 0
                NADI.paradigm -> 1
                STRI.paradigm -> 2
                DHI.paradigm -> 3
                VADHU.paradigm -> 4
                BHU.paradigm -> 5
                SUCI.paradigm -> 6
                MRDU.paradigm -> 7
                KARTR.paradigm -> 8
                PITR.paradigm -> 9
                SVASR.paradigm -> 10
                MATR.paradigm -> 11
                else -> 12
            }
        }
    }
}