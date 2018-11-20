package com.android.lvicto.sanskriter.data

import android.os.Parcel
import android.os.Parcelable


data class BookSection(val name: String,
                       val pages: List<String>) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.createStringArrayList())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeStringList(pages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookSection> {
        override fun createFromParcel(parcel: Parcel): BookSection {
            return BookSection(parcel)
        }

        override fun newArray(size: Int): Array<BookSection?> {
            return arrayOfNulls(size)
        }
    }
}