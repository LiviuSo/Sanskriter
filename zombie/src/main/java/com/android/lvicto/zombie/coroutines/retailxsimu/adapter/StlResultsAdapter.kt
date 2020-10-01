package com.android.lvicto.zombie.coroutines.retailxsimu.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.coroutines.retailxsimu.data.StlResult
import com.android.lvicto.zombie.coroutines.retailxsimu.ext.fetchImageAsBitmap
import com.android.lvicto.zombie.coroutines.retailxsimu.ext.runBlockingWith
import kotlinx.android.synthetic.main.item_stl_result.view.*
import kotlinx.coroutines.CoroutineExceptionHandler

class StlResultsAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _results: ArrayList<StlResult>? = null
    var results: ArrayList<StlResult>
        get() = _results ?: arrayListOf()
        set(value) {
            _results = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_stl_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ResultViewHolder).bind(results[position])
    }

    override fun getItemCount(): Int = results.size

    class ResultViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: StlResult) {
            // setup image
            view.stlResultImage.background = ContextCompat.getDrawable(view.context, R.drawable.image_placeholder)
            val exceptionHandler = CoroutineExceptionHandler { _, exception ->
                Log.d("retailx", "fetchImageAsBitmap: $exception")
                view.stlResultImage.setImageBitmap(null)
            }
            runBlockingWith(exceptionHandler = exceptionHandler) {
                view.stlResultImage.setImageBitmap(fetchImageAsBitmap(data.imageUrl))
                view.stlResultImage.background = null
            }
            view.stlResultTile.text = data.name
        }
    }
}