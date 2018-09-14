package com.android.lvicto.sanskriter.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.source.TitlesProvider
import com.android.lvicto.sanskriter.ui.activities.PageActivity
import java.util.ArrayList

class TitlesAdapter internal constructor(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: ArrayList<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.chapter_title, parent, false)
        return ChapterTitleView(item)
    }

    override fun getItemCount(): Int = if (data != null) {
        data!!.size
    } else {
        0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (data != null) {
            (holder as ChapterTitleView).bindData(data!![position],
                    getItemViewType(position),
                    getClickListenerChapter(position),
                    getClickListenerTitle())
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (data != null) {
                if (data!![position].toLowerCase().contains("Chapter", true))
                    TYPE_CHAPTER
                else
                    TYPE_SECTION
            } else {
                TYPE_NONE
            }

    private fun getClickListenerChapter(position: Int) = View.OnClickListener {
        Toast.makeText(context, "Tapped: ${data!![position]}", Toast.LENGTH_SHORT).show()
        val helper = TitlesProvider(data!!)
        // collapse if already expanded or just expand
        if (helper.isExpanded(position)) {
            helper.collapseData(position)
        } else {
            helper.expandData(position)
        }
        notifyDataSetChanged()
        Log.d(LOG_TAG, "new com.example.lvicto.coultersanskrit.data: $helper")
    }

    private fun getClickListenerTitle() = View.OnClickListener {
        // Toast.makeText(context, "Tapped section", Toast.LENGTH_SHORT).show() // todo remove
        with(context) {
            startActivity(Intent(this, PageActivity::class.java))
        }
    }

    companion object {
        private val LOG_TAG = TitlesAdapter::class.java.simpleName
        private const val TYPE_CHAPTER = 0
        private const val TYPE_SECTION = 1
        private const val TYPE_NONE = -1
    }

    class ChapterTitleView internal constructor(private val item: View) : RecyclerView.ViewHolder(item) {
        fun bindData(data: String, type: Int, listenerChapter: View.OnClickListener, listenerSection: View.OnClickListener) {
            item.apply {
                findViewById<TextView>(R.id.name).apply {
                    text = data
                    if (type == TYPE_CHAPTER) {
                        setOnClickListener(listenerChapter)
                    } else if (type == TYPE_SECTION) {
                        setOnClickListener(listenerSection)
                    }
                }
            }
        }
    }
}