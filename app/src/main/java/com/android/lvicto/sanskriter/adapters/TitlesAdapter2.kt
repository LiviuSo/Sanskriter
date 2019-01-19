package com.android.lvicto.sanskriter.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.utils.PreferenceHelper
import java.util.*

class TitlesAdapter2 internal constructor(private val context: Context,
                                          private val titleClickListener: View.OnClickListener,
                                          var isSearchOn: Boolean = false)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: ArrayList<String> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    private val lastOpenedSection: String = PreferenceHelper(context).getLastSection()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val item = if (viewType == TYPE_CHAPTER)
            LayoutInflater.from(parent.context).inflate(R.layout.item_chapter_title, parent, false)
        else { // section
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section_title, parent, false)
            if (viewType == TYPE_SECTION_SELECTED) {
                val selBackgroundColor = ContextCompat.getColor(context, R.color.sectionSelectedTitleColor)
                view.findViewById<TextView>(R.id.tvItemName).setBackgroundColor(selBackgroundColor)
            } else if (viewType == TYPE_SECTION_SEARCH) {
                val selBackgroundColor = ContextCompat.getColor(context, R.color.material_grey_100)
                val selTextColor = ContextCompat.getColor(context, R.color.colorPrimaryBook)
                val tv = view.findViewById<TextView>(R.id.tvItemName)
                tv.setBackgroundColor(selBackgroundColor)
                tv.setTextColor(selTextColor)
            }
            view
        }
        return ChapterTitleView(item)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChapterTitleView).bindData(data[position], titleClickListener)
    }

    // todo make string res
    override fun getItemViewType(position: Int): Int =
            when {
                data[position].toLowerCase().contains("Chapter", true) -> TYPE_CHAPTER
                data[position] == lastOpenedSection && !isSearchOn -> TYPE_SECTION_SELECTED
                isSearchOn -> TYPE_SECTION_SEARCH
                else -> TYPE_SECTION
            }

    companion object {
        private val LOG_TAG = TitlesAdapter2::class.java.simpleName
        private const val TYPE_CHAPTER = 0
        private const val TYPE_SECTION = 1
        private const val TYPE_SECTION_SEARCH = 2
        private const val TYPE_SECTION_SELECTED = 3
    }

    class ChapterTitleView internal constructor(private val item: View) : RecyclerView.ViewHolder(item) {
        fun bindData(data: String, titleClickListener: View.OnClickListener) {
            item.apply {
                tag = data // save the title in the tag for the click listener
                findViewById<TextView>(R.id.tvItemName).apply {
                    text = data
                    setOnClickListener(titleClickListener)
                }
            }
        }
    }
}