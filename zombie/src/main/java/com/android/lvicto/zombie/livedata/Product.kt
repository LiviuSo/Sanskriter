package com.android.lvicto.zombie.livedata

data class Product(val styleColor: String, val pid: String, val descr: String, val availability: Map<Int, List<Boolean>>) {
    override fun toString(): String {
        return StringBuffer()
                .append(descr)
                .append("\n")
                .append(styleColor)
                .append(":")
                .append(pid)
                .toString()
    }
}

