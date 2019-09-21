package com.android.lvicto.sanskriter.data

import android.os.Parcel
import android.os.Parcelable

data class BookContent(val title: String,
                       val chaptersCount: Int,
                       val sections: Map<Int, List<BookSection>>): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readInt(),
            parcel.readSections())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(chaptersCount)
        parcel.writeSections(sections)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookContent> {
        override fun createFromParcel(parcel: Parcel): BookContent {
            return BookContent(parcel)
        }

        override fun newArray(size: Int): Array<BookContent?> {
            return arrayOfNulls(size)
        }
    }
}

private fun Parcel.readSections(): Map<Int, List<BookSection>> {
    val map = sortedMapOf<Int, List<BookSection>>()
    val mapSize = readInt()
    (0 until mapSize).forEach {
        val key = readInt()
        val sections = arrayListOf<BookSection>()
        readList(sections as List<*>, BookSection::class.java.classLoader)
        map[key] = sections
    }
    return map
}

private fun Parcel.writeSections(map: Map<Int, List<BookSection>>) {
    val size = map.size
    writeInt(size)
    map.keys.forEach {
        writeInt(it)
        writeList(map[it])
    }
}
