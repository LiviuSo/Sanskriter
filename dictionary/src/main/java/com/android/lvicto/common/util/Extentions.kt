package com.android.lvicto.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.core.content.FileProvider
import java.io.*


fun Context.getStorageDir(fileName: String): String {
    // create folder
    val dir = File(this.getExternalFilesDir(null), "words/")
    if (!dir.mkdirs()) {
        dir.mkdirs()
    }
    // create file
    val file = File(dir.absolutePath + File.separator.toString(), fileName)
    file.createNewFile()
    return file.absolutePath
}

//write data to file
fun Context.writeDataToFile(data: String, fileName: String): Boolean { // todo make async
    var fileOutputStream: FileOutputStream? = null
    var res = false
    try {
        val filePath = this.getStorageDir(fileName)
        Log.d("readwrite", filePath)
        fileOutputStream = FileOutputStream(filePath)
        fileOutputStream.write(data.toByteArray())
        res = true
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        res = false
    } catch (e: IOException) {
        e.printStackTrace()
        res = false
    } finally {
        fileOutputStream?.close()
        return res
    }
}

//read data from the file
fun Context.readData(uri: Uri): String {
    val stringBuilder = StringBuilder()
    try {
        val fileInputStream = this.contentResolver.openInputStream(uri)
        val inputStreamReader = InputStreamReader(fileInputStream)
        val reader = BufferedReader(inputStreamReader)
        var temp: String?
        while (reader.readLine().also { temp = it } != null) {
            stringBuilder.append(temp)
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}

@Deprecated("remove")
fun Context.readData(fileName: String): String {
    val stringBuilder = StringBuilder()
    try {
        val filePath = this.getStorageDir(fileName)
//        val fileInputStream = FileInputStream(filePath)
//        var c = -1
//        do {
//            c = fileInputStream.read()
//            if( c != -1) {
//                stringBuilder.append(c.toChar())
//            } else {
//                break
//            }
//        } while (true)

        val file = File(filePath)
        val bufferedReader = BufferedReader(FileReader(file))
        var temp: String?
        while (bufferedReader.readLine().also { temp = it } != null) {
            stringBuilder.append(temp).append("\r\n")
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}

/**
 * Create an intent to share
 */
fun Context.export(filename: String, subject: String = "Dictionary: exporting data") {
    val file = File(this.getStorageDir(filename))
    val path = FileProvider.getUriForFile(
        this,
        this.applicationContext.packageName + ".provider",
        file
    )
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    // set the type to 'email'
    emailIntent.type = "vnd.android.cursor.dir/email"
    val to = arrayOf(Constants.Dictionary.EMAIL_RECIPIENT)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
    // the attachment
    emailIntent.putExtra(Intent.EXTRA_STREAM, path)
    // the mail subject
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    this.startActivity(Intent.createChooser(emailIntent, Constants.Dictionary.EMAIL_TITLE))
}


// todo use them
//checks if external storage is available for read and write
fun isExternalStorageAvailable(): Boolean {
    val state = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == state
}

//checks if external storage is available for read
fun isExternalStorageReadable(): Boolean {
    val state = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED_READ_ONLY == state
}


fun Spinner.initSpinner(@ArrayRes array: Int,
                        doOnSelect: (parent: AdapterView<*>?, Int) -> Unit,
                        doOnSelectNothing: () -> Unit) {
    this.adapter = ArrayAdapter.createFromResource(
        this.context,
        array,
        android.R.layout.simple_spinner_item
    ).also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            doOnSelect.invoke(parent, position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            doOnSelectNothing()
        }
    }
}

// Boolean
fun Boolean?.isFalse(): Boolean {
    return if (this == null) {
        true
    } else {
        this != true
    }
}

// Activity
fun Activity.openFilePicker(requestCode: Int) {
    var chooseFile = Intent()
    chooseFile.action = Intent.ACTION_GET_CONTENT
    chooseFile.type = "*/*"
    chooseFile = Intent.createChooser(chooseFile, "Choose a file")
    startActivityForResult(chooseFile, requestCode)
}





