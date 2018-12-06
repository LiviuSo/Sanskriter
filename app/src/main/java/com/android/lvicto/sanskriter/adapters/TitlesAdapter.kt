package com.android.lvicto.sanskriter.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.source.TitlesHelper
import com.android.lvicto.sanskriter.ui.activities.PagesActivity
import com.android.lvicto.sanskriter.data.BookContent
import java.util.ArrayList

class TitlesAdapter internal constructor(private val context: Context, bookContent: BookContent) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var helper: TitlesHelper = TitlesHelper(bookContent)
    private var data: ArrayList<String>

    init {
        data = helper.titles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val item = if(viewType == TYPE_CHAPTER)
            LayoutInflater.from(parent.context).inflate(R.layout.item_chapter_title, parent, false)
        else
            LayoutInflater.from(parent.context).inflate(R.layout.item_section_title, parent, false)
        return ChapterTitleView(item)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChapterTitleView).bindData(data[position],
                getItemViewType(position),
                getClickListenerChapter(position),
                getClickListenerTitle(data[position]))
    }

    // todo make string res
    override fun getItemViewType(position: Int): Int =
            if (data[position].toLowerCase().contains("Chapter", true))
                TYPE_CHAPTER
            else
                TYPE_SECTION

    private fun getClickListenerChapter(position: Int) = View.OnClickListener {
        // collapse if already expanded or just expand
        if (helper.isExpanded(position)) {
            helper.collapseData(position)
        } else {
            helper.expandData(position)
        }
        notifyDataSetChanged()
    }

    private fun getClickListenerTitle(title: String) = View.OnClickListener {
        // Toast.makeText(context, "Tapped section", Toast.LENGTH_SHORT).show() // todo remove
        with(context) {
            val intent = Intent(context, PagesActivity::class.java)
            intent.putExtra("section", helper.getSectionByTitle(title))
            startActivity(intent)
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
                findViewById<TextView>(R.id.tvItemName).apply {
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