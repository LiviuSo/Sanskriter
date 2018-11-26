package com.android.lvicto.sanskriter.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.db.entity.Word


class WordsAdapter(private val context: Context, private val clickListener: View.OnClickListener, private val longClickListener: View.OnLongClickListener) : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    var type: Int = TYPE_NON_REMOVABLE
    var words: List<Word>? = null
        set(value) {
            if (value != null) {
                field = value
                notifyDataSetChanged()
            }
        }
    val selectedToRemove = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsAdapter.WordViewHolder {
        val view = if (type == TYPE_NON_REMOVABLE) {
            LayoutInflater.from(context).inflate(R.layout.item_word, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_word_removable, parent, false)
        }
        return WordViewHolder(view, clickListener, longClickListener)
    }

    override fun getItemCount(): Int {
        return if (words != null) {
            words!!.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: WordsAdapter.WordViewHolder, position: Int) {
        if (words != null) {
            holder.bindData(words!![position], getItemViewType(position), position)
        } else {
            Log.e(LOG_TAG, "Empty or null data")
        }
    }

    override fun getItemViewType(position: Int): Int = type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun removeSelected(v: View) {
        selectedToRemove.forEach {
            val pos = getItemId(it).toInt()
            (words as ArrayList).removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, (words as java.util.ArrayList<Word>).size)
        }
        selectedToRemove.clear()
    }

    fun unselectRemoveSelected() {
        // todo
    }

    fun getWordsToRemove(): List<Word> = selectedToRemove.map {
        words!![it]
    }

    inner class WordViewHolder(val view: View,
                               private val clickListener: View.OnClickListener,
                               private val longClickListener: View.OnLongClickListener) : RecyclerView.ViewHolder(view) {

        fun bindData(word: Word, type: Int, position: Int) { // todo complete
            view.tag = word
            view.findViewById<TextView>(R.id.tvItemWordIAST).text = word.wordIAST
            view.findViewById<TextView>(R.id.tvItemWordSa).text = word.word
            view.setOnClickListener(clickListener)
            view.setOnLongClickListener(longClickListener)
            when (type) {
                TYPE_NON_REMOVABLE -> {
                    // do specifics
                }
                TYPE_REMOVABLE -> {
                    val checkBox = view.findViewById<CheckBox>(R.id.ckbItem)
                    checkBox.setOnCheckedChangeListener { cb, checked ->
                        if (!checked) {
                            selectedToRemove.remove(position)
                            Log.d(LOG_TAG, "De-selected $position")
                        } else {
                            selectedToRemove.add(position)
                            Log.d(LOG_TAG, "Selected $position")
                        }
                    }
                }
                else -> {
                    Log.d(LOG_TAG, "Unknown type of recycler view item ")
                }
            }
        }
    }

    companion object {
        const val TYPE_REMOVABLE = 1
        const val TYPE_NON_REMOVABLE = 2
        val LOG_TAG = WordsAdapter::class.java.toString()
    }

    init {
        this.setHasStableIds(true)
    }
}