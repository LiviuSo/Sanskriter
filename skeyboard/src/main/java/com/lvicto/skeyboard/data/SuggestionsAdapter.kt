package com.lvicto.skeyboard.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lvicto.skeyboard.KeyListeners
import com.lvicto.skeyboard.view.key.TypableKeyView
import com.example.skeyboard.R
import kotlinx.android.synthetic.main.item_suggestion.view.*

class SuggestionsAdapter(val context: Context) : RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {

    private var _data: List<Suggestion> = arrayListOf()
    var data: List<Suggestion>
        get() = _data
        set(value) {
            _data = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class SuggestionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(suggestion: Suggestion) {
            view.suggestion.text = suggestion.word
            (view.suggestion as TypableKeyView).label = suggestion.word
            view.suggestion.setOnClickListener(KeyListeners.getKeySuggestionClickListener())
        }
    }

    companion object {
        const val MIN_COUNT = 16

        val defaultSuggestions = arrayListOf(
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1),
                Suggestion(".", -1)
        )

    }
}