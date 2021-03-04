package com.android.lvicto.sanskriter.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.android.lvicto.sanskriter.MyApplication.Companion.application
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.ui.view.CursorWatcher
import com.android.lvicto.sanskriter.util.AssetsHelper
import com.android.lvicto.sanskriter.util.SandhiEngine
import com.android.lvicto.sanskriter.viewmodel.NotesViewModel
import com.android.lvicto.ui.DictionaryActivity
import com.android.lvicto.util.Constants.Dictionary.EXTRA_WORD_EN
import com.android.lvicto.util.Constants.Dictionary.EXTRA_WORD_IAST
import kotlinx.android.synthetic.main.fragment_zoom_page.*
import kotlinx.android.synthetic.main.fragment_zoom_page.view.*
import kotlinx.android.synthetic.main.search_bar.view.*
import kotlinx.android.synthetic.main.view_add_note.*
import kotlinx.android.synthetic.main.view_add_note.view.*
import kotlinx.android.synthetic.main.view_sandhi.*
import kotlinx.android.synthetic.main.view_sandhi.view.*
import kotlinx.android.synthetic.main.view_search_words.view.*

class  ZoomPageFragment : Fragment() {

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
    ): View? {
        viewParent = inflater.inflate(R.layout.fragment_zoom_page, container, false)
        AssetsHelper.loadAsset(viewParent.findViewById(R.id.zoomablePage), asset)

        notesViewModel = NotesViewModel(context = application) // todo injection

        // dic
        viewSearchDic = inflater.inflate(R.layout.view_search_words, container, false)
        viewSearchDic.btnSearch.setOnClickListener {
            val intent = Intent(activity, DictionaryActivity::class.java)
            intent.putExtra(EXTRA_WORD_IAST, viewSearchDic.edWordIast.text.toString())
            intent.putExtra(EXTRA_WORD_EN, viewSearchDic.edWordEn.text.toString())
            startActivity(intent)
        }
        viewSearchDic.btnAddWordCancel.setOnClickListener {
            bottomHolder.removeView(viewSearchDic)
            llBottomToolbar.visibility = View.VISIBLE
        }
        viewSearchDic.let { searchView ->
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
                viewParent.bottomHolder.addView(viewSearchDic)
            }
            llBottomToolbar.visibility = View.GONE
        }

        // note
        viewAddNote = inflater.inflate(R.layout.view_add_note, container, false)
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

        viewSandhi = inflater.inflate(R.layout.view_sandhi, container, false)
        viewSandhi.edComposite.addCursorWatcher(object : CursorWatcher {
            override fun onSelectionChanged(string: String, begin: Int, end: Int) {
                val words = decomposeSandhi(string, begin, end)
                viewSandhi.tvSeparate.text = words
            }
        })

        viewParent.btnSandhi.setOnClickListener {
            if (viewParent.findViewById<ConstraintLayout>(R.id.sandhi) == null) {
                viewParent.bottomHolder.addView(viewSandhi)
            }
            llBottomToolbar.visibility = View.GONE
        }

        return viewParent
    }

    private fun decomposeSandhi(composite: String, begin: Int, end: Int): String {
        if(begin < 1 || end > composite.length - 1 || begin > end) {
            return composite
        }
        val stringBuffer = StringBuffer().apply {
            SandhiEngine.decomposeSandhi(composite.substring(begin, end))?.map { sandhi ->
                append(composite.substring(0, begin))
                append(sandhi)
                append(composite.substring(end))
                append('\n')
            } ?: append(composite)
        }
        return stringBuffer.toString()
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
