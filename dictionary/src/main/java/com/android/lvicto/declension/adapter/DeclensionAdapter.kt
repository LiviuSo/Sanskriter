package com.android.lvicto.declension.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.R
import com.android.lvicto.common.Word
import com.android.lvicto.db.entity.Declension
import kotlinx.android.synthetic.main.item_grammar.view.*

class DeclensionAdapter(val context: Context, private val word: Word? = null) : RecyclerView.Adapter<DeclensionAdapter.DeclensionViewHolder>() {

    var onDeleteClick: ((Declension) -> Unit)? = null

    private var data: List<Declension>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeclensionViewHolder {
        return DeclensionViewHolder(
            view = LayoutInflater.from(context).inflate(R.layout.item_grammar, parent, false),
            word = word,
            onDeleteClick = onDeleteClick
        )
    }

    override fun onBindViewHolder(holder: DeclensionViewHolder, position: Int) {
        holder.bind(data?.get(position))
    }

    override fun getItemCount(): Int = data?.size ?: 0

    fun refresh(list: List<Declension>) {
        data = list
        notifyDataSetChanged()
    }

    // todo implement hiding x button is onDeleteClick is empty
    class DeclensionViewHolder(val view: View, val word: Word?, private val onDeleteClick: ((Declension) -> Unit)?): RecyclerView.ViewHolder(view) {
        private var declensionEngine = DeclensionEngine(view.context)

        fun bind(declension: Declension?) {
            view.tvDeclension.text = word?.let {
                val wordDeclensionRoot = declensionEngine.getWordDeclensionRoot(it)
                if(wordDeclensionRoot.isEmpty()) declension?.toString()
                else declension?.toString(wordDeclensionRoot)
            } ?: declension?.toString()

            view.btnRemove.apply {
                visibility = if(word != null) View.GONE else View.VISIBLE
                setOnClickListener {
                    declension?.let { onDeleteClick?.invoke(it) }
                }
            }
        }
    }
}