package com.android.lvicto.sanskriter.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.sanskriter.R
import kotlinx.android.synthetic.main.item_sandhi_suggestion.view.*

/**
 * Adapter
 */
class SandhiSuggestionAdapter(val context: Context, private val onOptionClick: (String) -> Unit) :
    RecyclerView.Adapter<SandhiSuggestionAdapter.SandhiSuggestionHolder>() {

    val data: ArrayList<SandhiSuggestion> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SandhiSuggestionHolder =
        SandhiSuggestionHolder(
            LayoutInflater.from(context).inflate(R.layout.item_sandhi_suggestion, parent, false),
            onOptionClick,
            { notifyDataSetChanged() }, { item ->
                data.remove(item)
                notifyDataSetChanged()
            }
        )

    override fun onBindViewHolder(holder: SandhiSuggestionHolder, position: Int) {
        holder.bind(data[position])
    }


    override fun getItemCount(): Int = data.size ?: 0

    fun saveSuggestions() {
        data.removeAll {
            it.type == Type.SUGGESTION_TO_REMOVE
        }
        data.forEach {
            if (it.type == Type.SUGGESTION) {
                it.type = Type.OPTION
            }
        }
        notifyDataSetChanged()
    }

    /**
     * remove suggestions marked for removing
     *
     */
    fun replaceSuggestions(suggs: ArrayList<SandhiSuggestion>? = null) {
        data.removeAll {
            it.type == Type.SUGGESTION || it.type == Type.SUGGESTION_TO_REMOVE
        }
        suggs?.forEach {
            if (!data.any { d -> d.string == it.string }) // add only if not saved already
                data.add(0, it)
        }
        notifyDataSetChanged()
    }

    fun saveTextForSandhi(text: String) {
        replaceSuggestions() // remove suggestion
        val optionFromText = SandhiSuggestion(text, Type.OPTION)
        if (!data.any { d -> d.string == text }) { // add only if not saved already
            data.add(0, optionFromText)
        }
    }

    /**
     * View holder
     */
    class SandhiSuggestionHolder(
        val view: View,
        private val onOptionClick: (String) -> Unit,
        private val onSuggestionClick: () -> Unit,
        private val onOptionClearClick: (SandhiSuggestion) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {
        fun bind(dataItem: SandhiSuggestion) {
            view.tvItemSandhiSuggestion.text = dataItem.string
            when (dataItem.type) {
                Type.SUGGESTION -> {
                    view.apply {
                        this.tvItemSandhiSuggestion.setStrikeThrough(false)
                        this.setBackgroundColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.colorSandhiSuggestion
                            )
                        )
                        this.setOnClickListener {
                            dataItem.type = Type.SUGGESTION_TO_REMOVE
                            this.tvItemSandhiSuggestion.setStrikeThrough()
                            onSuggestionClick()
                        }
                        this.ibRemoveOption.visibility = View.GONE
                    }
                }
                Type.OPTION -> {
                    view.apply {
                        this.tvItemSandhiSuggestion.setStrikeThrough(false)
                        this.setOnClickListener {
                            if (dataItem.type == Type.OPTION) {
                                onOptionClick(dataItem.string)
                            }
                        }
                        this.setBackgroundColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.colorSandhiOption
                            )
                        )
                        this.ibRemoveOption.apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                onOptionClearClick(dataItem)
                            }
                        }
                    }
                }
                Type.SUGGESTION_TO_REMOVE -> {
                    view.apply {
                        this.tvItemSandhiSuggestion.setStrikeThrough()
                        this.setBackgroundColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.colorSandhiSuggestionToRemove
                            )
                        )
                        this.setOnClickListener {
                            dataItem.type = Type.SUGGESTION
                            this.tvItemSandhiSuggestion.setStrikeThrough(false)
                            onSuggestionClick()
                        }
                        this.ibRemoveOption.visibility = View.GONE

                    }
                }
            }
        }
    }

    /**
     * Status
     */
    enum class Type {
        SUGGESTION,
        OPTION,
        SUGGESTION_TO_REMOVE
    }

    /**
     * Data
     */
    data class SandhiSuggestion(val string: String, var type: Type = Type.SUGGESTION)
}


fun TextView.setStrikeThrough(on: Boolean = true) {
    if (on) {
        this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        val mask = Paint.STRIKE_THRU_TEXT_FLAG.inv()
        this.paintFlags = this.paintFlags and mask
    }
}
