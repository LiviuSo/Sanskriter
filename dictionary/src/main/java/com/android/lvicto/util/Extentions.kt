package com.android.lvicto.util

import android.content.Context
import java.io.File


fun Context.getStorageDir(fileName: String): String {
    // create folder
    val dir = File(this.getExternalFilesDir("external"), "words/")
    if (!dir.mkdirs()) {
        dir.mkdirs()
    }
    // create file
    val file = File(dir.absolutePath + File.separator.toString(), fileName)
    file.createNewFile()
    return file.absolutePath
}
