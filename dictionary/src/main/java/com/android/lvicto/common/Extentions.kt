package com.android.lvicto.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.ArrayRes
import androidx.annotation.IdRes
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import com.android.lvicto.common.Constants.EMPTY_STRING
import java.io.*

/**
 * @param fileName The file name
 * @param parantName The name of the parent directory
 * @returns The path to the file
 */
fun Context.getFilePath(fileName: String, parentName: String = "words"): String {
    // create folder
    val dir = File(this.getExternalFilesDir(null), parentName + File.separator.toString())
    if (!dir.mkdirs()) {
        dir.mkdirs()
    }
    // create file
    val file = File(dir.absolutePath + File.separator.toString(), fileName)
    if(!file.exists()) {
        file.createNewFile()
    }
    return file.absolutePath
}

/**
 * @param data The data to write
 * @param fileName The name of the file
 * @return the path of the written file or empty string if error
 */
fun Context.writeDataToFile(data: String, fileName: String): String { // todo make async ?
    var fileOutputStream: FileOutputStream? = null
    try {
        return getFilePath(fileName).apply {
            fileOutputStream = FileOutputStream(this).apply {
                write(data.toByteArray())
            }
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        fileOutputStream?.close()
    }
    return EMPTY_STRING
}

/**
 * Read data from the file
 * @param uri The file
 * @return The read data as a string
 */
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

fun Context.readData(fileName: String): String {
    val stringBuilder = StringBuilder()
    try {
        val filePath = this.getFilePath(fileName, "notes")
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
fun Context.export(filePath: String, subject: String = "Dictionary: exporting data") {
    val path = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", File(filePath))
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        type = "vnd.android.cursor.dir/email"                               // set the type to 'email'
        putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.EMAIL_RECIPIENT))    // set the recipient
        putExtra(Intent.EXTRA_STREAM, path)                                 // set the attachment
        putExtra(Intent.EXTRA_SUBJECT, subject)                             // set the mail subject
    }
    startActivity(Intent.createChooser(emailIntent, Constants.EMAIL_TITLE))
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


fun Spinner.initSpinner(
    @ArrayRes array: Int,
    doOnSelect: (parent: AdapterView<*>?, Int) -> Unit,
    doOnSelectNothing: () -> Unit
) {
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

fun ActivityResultLauncher<Intent>.openFilePicker(codeHolder: ImportPickerCode, code: Int) {
    codeHolder.code = code
    Intent().apply {
        this.action = Intent.ACTION_GET_CONTENT
        this.type = "*/*"
        this@openFilePicker.launch(Intent.createChooser(this, "Select the dictionary file"))
    }
}

fun Activity.hideSoftKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

// View
fun View.navigate(@IdRes action: Int, bundle: Bundle? = null) {
    this.findNavController().navigate(action, bundle)
}

fun View.navigateBack() {
    this.findNavController().popBackStack()
}



