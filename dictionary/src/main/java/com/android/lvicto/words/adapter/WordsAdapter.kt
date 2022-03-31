package com.android.lvicto.words.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.R
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.db.entity.Word


class WordsAdapter(private val context: Context,
                   private val clickListenerDefinition: View.OnClickListener,
                   private val longClickListener: View.OnLongClickListener,
                   private val checkItemCallback: (Boolean) -> Unit) : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    var type: Int = TYPE_NON_REMOVABLE
    var words: List<Word>? = null
        set(value) {
            if (value != null) {
                field = value
                notifyDataSetChanged()
            }
        }
    val selectedToRemove = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder = WordViewHolder(
        if (type == TYPE_NON_REMOVABLE) { LayoutInflater.from(context).inflate(R.layout.item_word, parent, false) } else { LayoutInflater.from(context).inflate(R.layout.item_word_removable, parent, false) },
        clickListenerDefinition,
        longClickListener,
        checkItemCallback)

    override fun getItemCount(): Int = words?.size ?: 0

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        words?.let {
            holder.bindData(it[position], getItemViewType(position), position)
        } ?: run {
            Log.e(LOG_TAG, "Empty or null data")
        }
    }

    override fun getItemViewType(position: Int): Int = type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun unselectSelectedToRemove() {
        selectedToRemove.clear()
        notifyDataSetChanged() // todo try to optimize
    }

    fun getWordsToRemove(): List<Word> = selectedToRemove.map {
        words!![it]
    }

    inner class WordViewHolder(val view: View,
                               private val clickListenerDefinition: View.OnClickListener,
                               private val longClickListener: View.OnLongClickListener,
                               private val hideCtaOnNoSelection: (Boolean) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindData(word: Word, type: Int, position: Int) { // todo complete
            view.findViewById<TextView>(R.id.tvItemWordType).text = when(word.gType) {
                GrammaticalType.NOUN, GrammaticalType.ADJECTIVE -> "${word.gType.denom}, ${word.gender.abbr}, ${word.paradigm.ifEmpty { "n/a" }}"
                GrammaticalType.PROPER_NOUN -> "${word.gType.denom}, ${word.paradigm.ifEmpty { "n/a" }}"
                GrammaticalType.VERB -> "${word.gType.denom}, ${word.verbClass}"
                else -> word.gType.denom
            }
            view.findViewById<TextView>(R.id.tvItemWordIAST).text = word.wordIAST
            view.findViewById<TextView>(R.id.tvItemWordSa).text = word.word
            when (type) {
                TYPE_NON_REMOVABLE -> {
                    view.findViewById<TextView>(R.id.tvItemDefEn).text = word.meaningEn
                    view.findViewById<TextView>(R.id.tvItemDefRo).text = word.meaningRo
                    view.apply {
                        tag = word
                        setOnClickListener(clickListenerDefinition)
                    }
                    view.setOnLongClickListener(longClickListener)
                }
                TYPE_REMOVABLE -> {
                    val checkBox = view.findViewById<CheckBox>(R.id.ckbItem)
                    checkBox.apply {
                        setOnCheckedChangeListener { _, checked ->
                            if (!checked) {
                                Log.d(LOG_TAG, "De-selected $position")
                                selectedToRemove.remove(position)
                            } else {
                                Log.d(LOG_TAG, "Selected $position")
                                selectedToRemove.add(position)
                            }
                            hideCtaOnNoSelection(selectedToRemove.size == 0)
                        }
                    }
                    checkBox.isChecked = selectedToRemove.contains(position)
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