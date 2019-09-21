package com.android.lvicto.sanskriter.data

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList


data class BookSection(val name: String,
                       val pages: ArrayList<String>?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
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