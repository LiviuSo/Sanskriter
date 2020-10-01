package com.android.lvicto.zombie.coroutines.retailxsimu.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.*
import java.net.URL

fun <T> runBlockingWith(dispatcher: CoroutineDispatcher? = null, exceptionHandler: CoroutineExceptionHandler, block: suspend () -> T) {
    runBlocking {
        supervisorScope {
            if(dispatcher != null) {
                launch(dispatcher + exceptionHandler) {
                    block()
                }
            } else {
                launch(exceptionHandler) {
                    block()
                }
            }
        }
    }
}

suspend fun fetchImageAsBitmap(url: String): Bitmap? = coroutineScope {
    val def = async(Dispatchers.IO) {
        val ina = URL(url).openStream()
        BitmapFactory.decodeStream(ina)
    }
    def.await()
}