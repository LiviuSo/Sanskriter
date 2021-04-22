package com.android.lvicto.db

enum class GramaticalGender(val abbr: String) {
    NONE("n/a"),
    MASCULIN("M."),
    FEMININ("F."),
    NEUTER("N.");

    companion object {
        fun getValueFromAbbr(abbr: String) : GramaticalGender {
            return  when(abbr) {
                "M." -> MASCULIN
                "F." -> FEMININ
                "N." -> NEUTER
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
                "sg." -> SINGULAR
                "du." -> DUAL
                "pl." -> PLURAL
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
                "Nom."-> NOMINATIV
                "Acc." -> ACCUSATIV
                "Ins." -> INSTRUMENTAL
                "Dat." -> DATIV
                "Abl." -> ABLATIV
                "Gen." -> GENITIV
                "Loc." -> LOCATIV
                "Voc." -> VOCATIV
                else -> NONE
            }
        }
    }
}