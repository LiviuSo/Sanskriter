package com.android.lvicto.sanskriter.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.common.Constants.EXTRA_WORD_EN
import com.android.lvicto.common.Constants.EXTRA_WORD_IAST
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.adapter.SandhiSuggestionAdapter
import com.android.lvicto.sanskriter.ui.view.CursorWatcher
import com.android.lvicto.sanskriter.util.AssetsHelper
import com.android.lvicto.sanskriter.util.SandhiEngine
import com.android.lvicto.sanskriter.viewmodel.NotesViewModel
import com.android.lvicto.words.activities.WordsActivity
import kotlinx.android.synthetic.main.fragment_zoom_page.*
import kotlinx.android.synthetic.main.fragment_zoom_page.view.*
import kotlinx.android.synthetic.main.view_add_note.*
import kotlinx.android.synthetic.main.view_add_note.view.*
import kotlinx.android.synthetic.main.view_sandhi.view.*
import kotlinx.android.synthetic.main.view_search_words.view.*

class ZoomPageFragment : Fragment() {

    private var words: List<String>? = null
    private lateinit var asset: String
    private lateinit var viewSearchDic: View
    private lateinit var viewAddNote: View
    private lateinit var viewSandhi: View
    private lateinit var viewParent: View

    private var notesViewModel: NotesViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewParent = inflater.inflate(R.layout.fragment_zoom_page, container, false)
        AssetsHelper.loadAsset(viewParent.findViewById(R.id.zoomablePage), asset)

        notesViewModel = NotesViewModel(context = application) // todo injection

        // zone dic
        viewSearchDic = inflater.inflate(R.layout.view_search_words, container, false)
        setupDic(viewSearchDic)
        // end zone

        // zone note
        viewAddNote = inflater.inflate(R.layout.view_add_note, container, false)
        setupNote(viewAddNote)
        // endzone

        // zone sandhi
        viewSandhi = inflater.inflate(R.layout.view_sandhi, container, false)
        setupSandhi(viewSandhi)

        viewParent.btnSandhi.setOnClickListener {
            if (viewParent.findViewById<ConstraintLayout>(R.id.sandhi) == null) {
                viewParent.bottomHolder.addView(viewSandhi)
            }
            llBottomToolbar.visibility = View.GONE
        }
        // endzone

        return viewParent
    }

    private fun setupDic(viewSearchDic: View) {
        this.viewSearchDic.btnSearch.setOnClickListener {
            val intent = Intent(activity, WordsActivity::class.java)
            intent.putExtra(EXTRA_WORD_IAST, this.viewSearchDic.edWordIast.text.toString())
            intent.putExtra(EXTRA_WORD_EN, this.viewSearchDic.edWordEn.text.toString())
            startActivity(intent)
        }
        this.viewSearchDic.btnAddWordCancel.setOnClickListener {
            bottomHolder.removeView(this.viewSearchDic)
            llBottomToolbar.visibility = View.VISIBLE
        }
        this.viewSearchDic.let { searchView ->
            searchView.btnClearAll.setOnClickListener {
                searchView.edWordIast.text.clear()
                searchView.edWordEn.text.clear()
                searchView.edWordIast.requestFocus()
            }
            searchView.imClearIast.setOnClickListener {
                searchView.edWordIast.text.clear()
            }
            searchView.imClearEn.setOnClickListener {
                searchView.edWordEn.text.clear()
            }
        }

        viewParent.btnSearchDic.setOnClickListener {
            if (viewParent.findViewById<ConstraintLayout>(R.id.addSearchContainer) == null) {
                viewParent.bottomHolder.addView(this.viewSearchDic)
            }
            llBottomToolbar.visibility = View.GONE
        }
    }

    private fun setupNote(viewAddNote: View) {
        viewAddNote.btnSave.setOnClickListener {
            val noteText = editNote.text.toString()
            notesViewModel?.saveNote(noteText) // todo add spinner
        }
        viewAddNote.btnCancel.setOnClickListener {
            bottomHolder.removeView(viewAddNote)
            llBottomToolbar.visibility = View.VISIBLE
        }
        viewParent.btnAddNote.setOnClickListener {
            if (viewParent.findViewById<ConstraintLayout>(R.id.addNoteContainer) == null) {
                val noteText = notesViewModel?.readNote() ?: "" // todo async
                viewAddNote.editNote.setText(noteText)
                viewParent.bottomHolder.addView(viewAddNote)
            }
            llBottomToolbar.visibility = View.GONE
        }
    }

    private fun setupSandhi(viewSandhi: View) {
        viewSandhi.rvPotentials.layoutManager = LinearLayoutManager(context)
        viewSandhi.rvPotentials.adapter = activity?.let {
            SandhiSuggestionAdapter(it) { string ->
                viewSandhi.edComposite.setText(string)
            }
        }

        viewSandhi.edComposite.addCursorWatcher(object : CursorWatcher {
            override fun onSelectionChanged(string: String, begin: Int, end: Int) {
                words = decomposeSandhi(string, begin, end)
                val suggs = arrayListOf<SandhiSuggestionAdapter.SandhiSuggestion>()
                words?.forEach {
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
            Toast.makeText(this@ZoomPageFragment.context, words.toString(), Toast.LENGTH_SHORT)
                .show()
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

    companion object {
        @JvmStatic
        fun newInstance(asset: String): ZoomPageFragment {
            val instance = ZoomPageFragment()
            instance.asset = asset
            return instance
        }
    }
}
