package com.android.lvicto.words.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
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
import com.android.lvicto.common.base.BaseActivity
import com.android.lvicto.common.Constants.CODE_REQUEST_ADD_WORD
import com.android.lvicto.common.Constants.CODE_REQUEST_EDIT_WORD
import com.android.lvicto.common.Constants.EXTRA_REQUEST_CODE
import com.android.lvicto.common.Constants.EXTRA_WORD
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.hideSoftKeyboard
import com.android.lvicto.common.navigate
import com.android.lvicto.common.base.BaseTextWatcher
import com.android.lvicto.common.base.BaseObservableMvc
import com.android.lvicto.db.entity.Word
import com.android.lvicto.words.adapter.WordsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_words.view.*
import kotlinx.android.synthetic.main.layout_all_words.view.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_search_bar.view.*

class WordsViewMvcImpl(
    private val mActivity: BaseActivity,
    inflater: LayoutInflater,
    container: ViewGroup?,
    dlgManager: DialogManager
) : BaseObservableMvc<WordsViewMvc.WordsViewListener>(), WordsViewMvc {

    private var mBtnCloseSearchBar: ImageButton
    private var mLlRemoveCancel: LinearLayout
    private var mFabDictionary: FloatingActionButton
    private var mRvWords: RecyclerView
    private var mToolbar: Toolbar
    private var mEditSearchIast: EditText
    private var mEditSearch: EditText
    private var mLlSearchBar: LinearLayout
    private var mDialogManager: DialogManager
    private var mProgress: ProgressBar
    private lateinit var mResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var mResources: Resources
    private lateinit var mWordsAdapter: WordsAdapter
    private var mClRootWords: CoordinatorLayout

    init {
        setRootView(inflater.inflate(R.layout.fragment_words, container, false))

        mLlSearchBar = getRootView().llSearchBar
        mEditSearch = getRootView().editSearch
        mEditSearchIast = getRootView().editSearchIast
        mToolbar = getRootView().toolbar
        mRvWords = getRootView().rv_words
        mFabDictionary = getRootView().fabDictionary
        mLlRemoveCancel = getRootView().llRemoveCancel
        mBtnCloseSearchBar = getRootView().btnCloseSearchBar
        mDialogManager = dlgManager
        mClRootWords = getRootView().clRootWords
        mProgress = getRootView().progress

        init()
    }

    override fun onFilePicked(data: Intent?) {
        val uri = data?.data
        for (listener in listeners) {
            listener.onReadFromFile(uri)
        }
    }

    override fun setWords(words: List<Word>?) {
        mWordsAdapter.words = words
    }

    override fun isSearchVisible(): Boolean = mLlSearchBar.visibility == View.VISIBLE

    override fun getSearchEnString(): String = mEditSearch.text.toString()

    override fun getSearchIastString(): String = mEditSearchIast.text.toString()

    private fun init() {
        mResources = requireActivity().resources
        initToolbar()
        initSearchBar() // todo separate view
        initRecView()
        initRemoveWords()
        initFab()
    }

    override fun setupSearchFromOutside(searchIast: String?, searchEn: String?) {
        if (!searchIast.isNullOrEmpty() || !searchEn.isNullOrEmpty()) {
            if (!isSearchVisible()) {
                // show edit with close button
                showSearchBar()
            }
            if (!searchIast.isNullOrEmpty()) {
                mEditSearchIast.setText(searchIast)
                for (listener in listeners) {
                    listener.onFilterIastEn(searchIast, searchEn)
                }
            }
            if (!searchEn.isNullOrEmpty()) {
                mEditSearch.setText(searchEn)
            }
        }
    }

    private fun initFab() {
        mFabDictionary.setOnClickListener {
            val word = Word(
                word = "",
                wordIAST = getSearchIastString(),
                meaningEn = getSearchEnString(),
                meaningRo = ""
            )
            val bundle = Bundle().apply {
                this.putParcelable(EXTRA_WORD, word)
                this.putInt(EXTRA_REQUEST_CODE, CODE_REQUEST_ADD_WORD)
            }
            it.findNavController()
                .navigate(R.id.action_dictionaryWordsFragment_to_addModifyFragment, bundle)
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
            val bundle = Bundle().apply {
                this.putParcelable(EXTRA_WORD, it.tag as Word)
                this.putInt(EXTRA_REQUEST_CODE, CODE_REQUEST_EDIT_WORD)
            }
            it.navigate(R.id.action_dictionaryWordsFragment_to_addModifyFragment, bundle)
        }

        @SuppressLint("RestrictedApi")
        val longClickListener: View.OnLongClickListener = View.OnLongClickListener {
            updateRevViewItems(WordsAdapter.TYPE_REMOVABLE)
            mToolbar.navigationUp.visibility = View.VISIBLE
            mFabDictionary.visibility = View.GONE
            true
        }

        mWordsAdapter = WordsAdapter(
            requireActivity(),
            itemEditClickListener,
            longClickListener
        ) {
            mLlRemoveCancel.visibility = if (it) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        mRvWords.layoutManager = LinearLayoutManager(requireActivity())
        mRvWords.adapter = mWordsAdapter
        for (listener in listeners) {
            listener.onInitWords()
        }
    }

    private fun initSearchBar() {
        mEditSearch.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filterEn = s.toString()
                val filterIast = getSearchIastString()
                for (listener in listeners) {
                    listener.onFilterEnIast(filterEn, filterIast)
                }
            }
        })

        mEditSearch.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val filterEn = (v as EditText).text.toString()
                val filterIast = getSearchIastString()
                for (listener in listeners) {
                    listener.onFilterEnIast(filterEn, filterIast)
                }
            }
        }

        mEditSearchIast.addTextChangedListener(object : BaseTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filterEn = getSearchEnString()
                val filterIast = s.toString()
                for (listener in listeners) {
                    listener.onFilterEnIast(filterEn, filterIast)
                }
            }
        })

        mEditSearchIast.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val filterEn = getSearchEnString()
                val filterIast = (v as EditText).text.toString()
                for (listener in listeners) {
                    listener.onFilterEnIast(filterEn, filterIast)
                }
            }
        }

        mBtnCloseSearchBar.setOnClickListener {
            clearSearch()
            // close the keyboard
            requireActivity().hideSoftKeyboard()
        }

        getRootView().ibSearchForm.setOnClickListener {
            val form = getRootView().edForm.text.toString()
            Toast.makeText(requireActivity(), "Search form $form", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initToolbar() {
        mToolbar.title = mResources.getString(R.string.dictionary)
        mToolbar.toolbarSearch.setOnClickListener {
            if (!isSearchVisible()) {
                // show edit with close button
                showSearchBar()
                // pop-up IAST keyboard for now
                // as typing, filter
            }
        }
        mToolbar.navigationUp.setOnClickListener {
            if (mWordsAdapter.type != WordsAdapter.TYPE_NON_REMOVABLE) {
                cancelRemoveSelected()
                it.visibility = View.GONE
            } else {
                requireActivity().onBackPressed()
            }
        }

        mToolbar.toolbarTitle.text = "Dictionary" // todo resource
        val overflowMenuImageView: ImageView = mToolbar.toolbarMenu
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
        TransitionManager.beginDelayedTransition(mClRootWords, transition)
        mLlSearchBar.visibility = View.VISIBLE
    }

    private fun requireActivity(): BaseActivity {
        return mActivity
    }

    private fun updateRevViewItems(type: Int) {
        val adapter: WordsAdapter = mRvWords.adapter as WordsAdapter
        adapter.type = type
        adapter.notifyDataSetChanged()
    }

    private fun clearSearch() {
        mEditSearch.text.clear()
        mEditSearchIast.text.clear()
        mLlSearchBar.visibility = View.GONE
    }

    private fun unselectAll(v: View) {
        (mRvWords.adapter as WordsAdapter).unselectSelectedToRemove()  // todo fix
        hideRemoveCancel()
    }

    @SuppressLint("CheckResult", "RestrictedApi")
    private fun removeSelected(v: View) {
        val adapter = mRvWords.adapter as WordsAdapter
        val wordsToRemove = adapter.getWordsToRemove()
        for (listener in listeners) {
            listener.onDeleteWords(wordsToRemove)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun cancelRemoveSelected() {
        updateRevViewItems(WordsAdapter.TYPE_NON_REMOVABLE)
        hideRemoveCancel()
        mFabDictionary.visibility = View.VISIBLE
    }

    private fun hideRemoveCancel() {
        mLlRemoveCancel.visibility = View.GONE
    }

    override fun setResultLauncher(resultLauncher: ActivityResultLauncher<Intent>) {
        mResultLauncher = resultLauncher
    }

    override fun unselectSelectedToRemove() {
        mWordsAdapter.unselectSelectedToRemove()
    }

    override fun showProgress() {
        mProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        mProgress.visibility = View.GONE
    }
}
