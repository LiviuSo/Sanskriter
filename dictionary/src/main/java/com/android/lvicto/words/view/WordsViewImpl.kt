package com.android.lvicto.words.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.R
import com.android.lvicto.common.Constants.CODE_REQUEST_ADD_WORD
import com.android.lvicto.common.Constants.CODE_REQUEST_EDIT_WORD
import com.android.lvicto.common.Constants.MODE_EDIT_WORD
import com.android.lvicto.common.Constants.MODE_VIEW_WORD
import com.android.lvicto.common.Word
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.base.ObservableMvcImpl
import com.android.lvicto.common.base.TextWatcherImpl
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.hideSoftKeyboard
import com.android.lvicto.common.navigate
import com.android.lvicto.db.data.GrammaticalType
import com.android.lvicto.words.adapter.WordsAdapter
import com.android.lvicto.words.fragments.WordDetailsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_words.view.*
import kotlinx.android.synthetic.main.layout_all_words.view.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_search_bar.view.*

class WordsViewImpl(
    private val activity: BaseActivity,
    inflater: LayoutInflater,
    container: ViewGroup?,
    dlgManager: DialogManager
) : ObservableMvcImpl<WordsView.WordsViewListener>(), WordsView {

    private var btnCloseSearchBar: ImageButton
    private var llRemoveCancel: LinearLayout
    private var fabDictionary: FloatingActionButton
    private var rvWords: RecyclerView
    private var toolbar: Toolbar
    private var editSearchIAST: EditText
    private var editSearch: EditText
    private var llSearchBar: LinearLayout
    private var dialogManager: DialogManager
    private var progress: ProgressBar
    private var clRootWords: CoordinatorLayout
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var resources: Resources
    private lateinit var wordsAdapter: WordsAdapter

    private val qwertyTextWatcher = object : TextWatcherImpl() {
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            triggerFiltering(p0.toString(), getSearchIASTString())
        }
    }

    private val iastTextWatcher = object : TextWatcherImpl() {
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            triggerFiltering(getSearchEnString(), p0.toString())
        }
    }

    init {
        setRootView(inflater.inflate(R.layout.fragment_words, container, false))

        llSearchBar = getRootView().llSearchBar
        editSearch = getRootView().editSearch
        editSearchIAST = getRootView().editSearchIast
        toolbar = getRootView().toolbar
        rvWords = getRootView().rv_words
        fabDictionary = getRootView().fabDictionary
        llRemoveCancel = getRootView().llRemoveCancel
        btnCloseSearchBar = getRootView().btnCloseSearchBar
        dialogManager = dlgManager
        clRootWords = getRootView().clRootWords
        progress = getRootView().progress

        init()
    }

    override fun onFilePicked(data: Intent?) {
        val uri = data?.data
        for (listener in listeners) {
            listener.onReadFromFile(uri)
        }
    }

    override fun setWords(words: List<Word>?) {
        wordsAdapter.words = words
    }

    override fun isSearchVisible(): Boolean = llSearchBar.visibility == View.VISIBLE

    override fun getSearchEnString(): String = editSearch.text.toString()

    override fun getSearchIASTString(): String = editSearchIAST.text.toString()

    private fun init() {
        resources = requireActivity().resources
        initToolbar()
        initSearchBar() // todo separate view
        initRecView()
        initRemoveWords()
        initFab()
    }

    override fun setupSearchFromOutside(searchIast: String?, searchEn: String?) {
        if (!searchIast.isNullOrEmpty()) {
            editSearchIAST.setText(searchIast)
        }
        if (!searchEn.isNullOrEmpty()) {
            editSearch.setText(searchEn)
        }
        if (!isSearchVisible() && (!searchIast.isNullOrEmpty() || !searchEn.isNullOrEmpty())) {
            showSearchBar()
        } else if(isSearchVisible()) {
            triggerFiltering(editSearch.text.toString(), editSearchIAST.text.toString())
        } else {
            triggerFiltering("", "")
        }
    }

    private fun initFab() {
        fabDictionary.setOnClickListener {
            val word = Word(
                gType = GrammaticalType.OTHER,
                wordSa = "",
                wordIAST = getSearchIASTString(),
                meaningEn = getSearchEnString(),
                meaningRo = ""
            )
            it.findNavController()
                .navigate(R.id.action_dictionaryWordsFragment_to_addModifyFragment, WordDetailsFragment.createBundle(word, CODE_REQUEST_ADD_WORD, MODE_EDIT_WORD))
        }
    }

    private fun initRemoveWords() {
        getRootView().let { root ->
            root.llRemoveCancel.let {
                it.btnRemove.setOnClickListener(this::removeSelected)
                it.btnClearSelections.setOnClickListener(this::unselectAll)
            }
        }
    }

    private fun initRecView() {
        val itemEditClickListener = View.OnClickListener {
            it.navigate(R.id.action_dictionaryWordsFragment_to_addModifyFragment, WordDetailsFragment.createBundle(it.tag as Word, CODE_REQUEST_EDIT_WORD, MODE_VIEW_WORD))
        }

        @SuppressLint("RestrictedApi")
        val longClickListener: View.OnLongClickListener = View.OnLongClickListener {
            updateRevViewItems(WordsAdapter.TYPE_REMOVABLE)
            toolbar.navigationUp.visibility = View.VISIBLE
            fabDictionary.visibility = View.GONE
            true
        }

        wordsAdapter = WordsAdapter(
            requireActivity(),
            itemEditClickListener,
            longClickListener
        ) {
            llRemoveCancel.visibility = if (it) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        rvWords.layoutManager = LinearLayoutManager(requireActivity())
        rvWords.adapter = wordsAdapter
        for (listener in listeners) {
            listener.onInitWords()
        }
    }

    private fun initSearchBar() {
        setEditsListeners()
        btnCloseSearchBar.setOnClickListener {
            hideSearch()
        }
    }

    private fun setEditsListeners() {
        editSearch.apply {
            addTextChangedListener(qwertyTextWatcher)
        }
        editSearchIAST.apply {
            addTextChangedListener(iastTextWatcher)
        }
    }

    private fun initToolbar() {
        toolbar.title = resources.getString(R.string.dictionary)
        toolbar.toolbarSearch.setOnClickListener {
            if (!isSearchVisible()) {
                // show edit with close button
                showSearchBar()
                // pop-up IAST keyboard for now
                // as typing, filter
            }
        }
        toolbar.navigationUp.setOnClickListener {
            if (wordsAdapter.type != WordsAdapter.TYPE_NON_REMOVABLE) {
                cancelRemoveSelected()
                it.visibility = View.GONE
            } else {
                requireActivity().onBackPressed()
            }
        }

        toolbar.toolbarTitle.text = resources.getString(R.string.dictionary)
        val overflowMenuImageView: ImageView = toolbar.toolbarMenu
        overflowMenuImageView.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireActivity(), view)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuItemImport -> {
                        for (listener in listeners) {
                            listener.onImport()
                        }
                        true
                    }
                    R.id.menuItemExport -> {
                        for (listener in listeners) {
                            listener.onExport()
                        }
                        true
                    }
                    R.id.menuItemFind -> {
                        if (!isSearchVisible()) {
                            // show edit with close button
                            showSearchBar()
                            clearSearch()
                            // pop-up IAST keyboard for now
                            // as typing, filter
                            true
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu_dictionary)
            popupMenu.show()
        }
    }

    private fun showSearchBar() {
        val transition: Transition = Fade()
        transition.duration = 250L
        TransitionManager.beginDelayedTransition(clRootWords, transition)
        llSearchBar.visibility = View.VISIBLE
        setEditsListeners()
        triggerFiltering(getSearchEnString(), getSearchIASTString())
    }

    private fun triggerFiltering(filterEn: String, filterIAST: String) {
        for (listener in listeners) {
            listener.onFilterEnIAST(filterEn, filterIAST)
        }
    }

    private fun requireActivity(): BaseActivity = activity

    @SuppressLint("NotifyDataSetChanged") // todo investigate
    private fun updateRevViewItems(type: Int) {
        val adapter: WordsAdapter = rvWords.adapter as WordsAdapter
        adapter.type = type
        adapter.notifyDataSetChanged()
    }

    private fun hideSearch() {
        requireActivity().hideSoftKeyboard() // close the keyboard
        showProgress()
        clearSearch()
        editSearch.apply {
            removeTextChangedListener(qwertyTextWatcher)
        }
        editSearchIAST.apply {
            removeTextChangedListener(iastTextWatcher)
        }
        llSearchBar.visibility = View.GONE
        hideProgress()
    }

    private fun clearSearch() {
        editSearch.text.clear()
        editSearchIAST.text.clear()
    }

    private fun unselectAll(v: View) {
        (rvWords.adapter as WordsAdapter).unselectSelectedToRemove()  // todo fix
        hideRemoveCancel()
    }

    @SuppressLint("CheckResult", "RestrictedApi")
    private fun removeSelected(v: View) {
        val adapter = rvWords.adapter as WordsAdapter
        val wordsToRemove = adapter.getWordsToRemove()
        for (listener in listeners) {
            listener.onDeleteWords(wordsToRemove)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun cancelRemoveSelected() {
        updateRevViewItems(WordsAdapter.TYPE_NON_REMOVABLE)
        hideRemoveCancel()
        fabDictionary.visibility = View.VISIBLE
    }

    private fun hideRemoveCancel() {
        llRemoveCancel.visibility = View.GONE
    }

    override fun setResultLauncher(resultLauncher: ActivityResultLauncher<Intent>) {
        this.resultLauncher = resultLauncher
    }

    override fun unselectSelectedToRemove() {
        wordsAdapter.unselectSelectedToRemove()
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }
}