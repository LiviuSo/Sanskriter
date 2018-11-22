package com.android.lvicto.sanskriter.utils

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log

object AssetsHelper {

    const val LOG_TAG = "AssetsHelper"

    fun getDrawableFromAssets(context: Context, path: String): Drawable? =
            if (path.isEmpty()) {
                null
            } else {
                try {
                    BitmapDrawable.createFromStream(context.assets.open(path), null)
                } catch (e: Exception) {
                    Log.d(LOG_TAG, "PageFragment::getDrawableFromAssets() + ${e.message}")
                    null
                }
            }
}