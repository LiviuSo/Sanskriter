package com.android.lvicto.repo

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.*


object FileRepository {

    private val LOG_TAG = FileRepository::class.simpleName
    private const val FILENAME: String = "sanskrit_dic.json"

    //write data to file
    fun writeData(context: Context, data: String, fileName: String): Boolean { // todo make async
        var result = true
        try {
            val filePath = getStorageDir(context, fileName)
            Log.d("readwrite", filePath)
            val fileOutputStream = FileOutputStream(filePath)
            fileOutputStream.write(data.toByteArray())
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            result = false
        } catch (e: IOException) {
            e.printStackTrace()
            result = false
        } finally {
            return result
        }
    }

    //read data from the file
    fun readData(context: Context, fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val filePath = getStorageDir(context = context, fileName = fileName)
            val fileInputStream = FileInputStream(filePath)
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

    fun getStorageDir(context: Context, fileName: String): String {
        //create folder
        val dir = File(context.getExternalFilesDir("external"), "words/")
        if (!dir.mkdirs()) {
            dir.mkdirs()
        }
        val file = File(dir.absolutePath + File.separator.toString(), fileName)
        file.createNewFile()
        return file.absolutePath
    }
}