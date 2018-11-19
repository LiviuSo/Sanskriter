package com.android.lvicto.sanskriter.data

data class BookContent(val title: String,
                       val chaptersCount: Int,
                       val sections: Map<Int, List<BookSection>>)
