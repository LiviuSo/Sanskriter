package com.android.lvicto.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.R
import com.android.lvicto.common.db.entity.Declension
import kotlinx.android.synthetic.main.item_grammar.view.*

class DeclensionAdapter(val context: Context) : RecyclerView.Adapter<DeclensionAdapter.DeclensionViewHolder>() {

    var onDeleteClick: ((Declension) -> Unit)? = null

    private var data: List<Declension>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeclensionViewHolder {
        return DeclensionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_grammar, parent, false), onDeleteClick)
    }

    override fun onBindViewHolder(holder: DeclensionViewHolder, position: Int) {
        holder.bind(data?.get(position))
    }

    override fun getItemCount(): Int = data?.size ?: 0

    fun refresh(list: List<Declension>) {
        data = list
        notifyDataSetChanged()
    }

    class DeclensionViewHolder(val view: View, private val onDeleteClick: ((Declension) -> Unit)?): RecyclerView.ViewHolder(view) {
        fun bind(declension: Declension?) {
            view.tvDeclension.text = declension.toString()
            view.btnRemove.setOnClickListener {
                declension?.let { it1 -> onDeleteClick?.invoke(it1) }
            }
        }
    }
}