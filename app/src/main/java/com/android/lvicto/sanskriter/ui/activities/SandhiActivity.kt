package com.android.lvicto.sanskriter.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapter.SandhiSuggestionAdapter
import com.android.lvicto.sanskriter.ui.view.CursorWatcher
import com.android.lvicto.sanskriter.util.SandhiEngine
import kotlinx.android.synthetic.main.view_sandhi.*
import kotlinx.android.synthetic.main.view_sandhi.view.*

class SandhiActivity : AppCompatActivity() {
    private var words: List<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandhi)

        setupSandhi(sandhi)
    }

    private fun setupSandhi(viewSandhi: View) {
        viewSandhi.rvPotentials.layoutManager = LinearLayoutManager(this)
        viewSandhi.rvPotentials.adapter = SandhiSuggestionAdapter(this) { string ->
            viewSandhi.edComposite.setText(string)
        }

        viewSandhi.edComposite.addCursorWatcher(object : CursorWatcher {
            override fun onSelectionChanged(string: String, begin: Int, end: Int) {
                words = decomposeSandhi(string, begin, end)
                val suggs = arrayListOf<SandhiSuggestionAdapter.SandhiSuggestion>()
                words.forEach {
                    if (it.isNotEmpty()) {
                        suggs.add(SandhiSuggestionAdapter.SandhiSuggestion(it))
                    }
                }
                (viewSandhi.rvPotentials.adapter as SandhiSuggestionAdapter).replaceSuggestions(
                    suggs
                )
            }
        })
        viewSandhi.ibSaveOptions.setOnClickListener {
            (viewSandhi.rvPotentials.adapter as SandhiSuggestionAdapter).saveSuggestions()
        }
        viewSandhi.ibClearText.setOnClickListener {
            val text = viewSandhi.edComposite.text
            if (text?.isNotEmpty() == true && text.isNotBlank()) {
                (viewSandhi.rvPotentials.adapter as SandhiSuggestionAdapter).saveTextForSandhi(text.toString()) // save the text as an option
                text.clear()
            }
        }
    }

    private fun decomposeSandhi(composite: String, begin: Int, end: Int): List<String> {
        if (begin < 1 || end > composite.length - 1 || begin > end) {
            return arrayListOf(composite)
        }
        return SandhiEngine.decomposeSandhi(composite.substring(begin, end))?.map { sandhi ->
            StringBuffer().let {
                it.append(composite.substring(0, begin))
                it.append(sandhi)
                it.append(composite.substring(end))
            }.toString()
        } ?: arrayListOf(composite)
    }

}