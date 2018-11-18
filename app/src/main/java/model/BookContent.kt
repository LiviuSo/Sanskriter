package model

data class BookContent(val title: String,
                       val chaptersCount: Int,
                       val sections: Map<Int, Array<String>>)
