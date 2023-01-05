package com.android.lvicto.words.view

import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.R
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.TextWatcherImpl
import com.android.lvicto.common.base.ViewMvcImpl
import com.android.lvicto.db.entity.Declension
import com.android.lvicto.declension.adapter.DeclensionAdapter
import kotlinx.android.synthetic.main.fragment_word_grammar.view.*

class WordGrammarViewImpl(val activity: BaseActivity,
                          private val word: Word,
                          containerView: ViewGroup?,
                          inflater: LayoutInflater
) : ViewMvcImpl<WordGrammarView.Listener>(), WordGrammarView {

    private lateinit var declensionAdapter: DeclensionAdapter

    init {
        setRootView(inflater.inflate(R.layout.fragment_word_grammar, containerView, false))
        setupDeclensionFiltering()
    }

    override fun registerListener(listener: WordGrammarView.Listener) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: WordGrammarView.Listener) {
        listeners.remove(listener)
    }

    override fun setDeclensions(declensions: List<Declension>) {
        declensionAdapter.refresh(declensions)
    }

    private fun setupDeclensionFiltering() {
        val root = getRootView()
        root.recyclerViewDeclensions.apply {
            layoutManager = LinearLayoutManager(activity)
            declensionAdapter = DeclensionAdapter(activity, word).apply {
                onDeleteClick = {
                    // nothing
                }
            }
            adapter = declensionAdapter
        }
        root.editTextDetectDeclensionWord.apply {
            addTextChangedListener(object : TextWatcherImpl() {
                override fun afterTextChanged(s: Editable?) {
                    for(l in listeners) {
                        l.onDeclensionSearchKeyChanged(s.toString(), word)
                    }
                }
            })
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    for(l in listeners) {
                        l.onDeclensionSearchKeyChanged(text.toString(), word)
                    }
                }
            }
            requestFocus()

            for(l in listeners) {
                l.onDeclensionSearchKeyChanged(root.editTextDetectDeclensionWord.text.toString(), word)
            }
        }
    }

}