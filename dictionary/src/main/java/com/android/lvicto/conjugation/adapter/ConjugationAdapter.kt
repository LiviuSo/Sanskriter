package com.android.lvicto.conjugation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.R
import com.android.lvicto.db.entity.Conjugation
import kotlinx.android.synthetic.main.item_grammar.view.*

class ConjugationAdapter(val context: Context) : RecyclerView.Adapter<ConjugationAdapter.ConjugationViewHolder>() {

    var onDeleteClick: ((Conjugation) -> Unit)? = null

    private var data: List<Conjugation>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConjugationViewHolder {
        return ConjugationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_grammar, parent, false), onDeleteClick)
    }

    override fun onBindViewHolder(holder: ConjugationViewHolder, position: Int) {
        holder.bind(data?.get(position))
    }

    override fun getItemCount(): Int = data?.size ?: 0

    fun refresh(list: List<Conjugation>) {
        data = list
        notifyDataSetChanged()
    }

    class ConjugationViewHolder(val view: View, private val onDeleteClick: ((Conjugation) -> Unit)?): RecyclerView.ViewHolder(view) {
        fun bind(declension: Conjugation?) {
            view.tvDeclension.text = declension.toString()
            view.btnRemove.setOnClickListener {
                declension?.let { it1 -> onDeleteClick?.invoke(it1) }
            }
        }
    }
}