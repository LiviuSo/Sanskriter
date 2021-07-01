package com.android.lvicto.words.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.lvicto.R
import com.android.lvicto.common.adapter.WordsAdapter
import com.android.lvicto.common.dialog.DialogManager
import com.android.lvicto.common.fragment.BaseFragment
import com.android.lvicto.common.util.Constants
import com.android.lvicto.common.util.Utils
import com.android.lvicto.common.util.export
import com.android.lvicto.common.util.openFilePicker
import com.android.lvicto.db.data.Words
import com.android.lvicto.db.entity.Word
import com.android.lvicto.words.WordsViewModel
import com.android.lvicto.words.activities.AddModifyWordActivity
import com.android.lvicto.words.activities.WordsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_words.view.*
import kotlinx.android.synthetic.main.layout_all_words.view.*
import kotlinx.android.synthetic.main.search_bar.*
import kotlinx.android.synthetic.main.search_bar.view.*
import kotlinx.android.synthetic.main.toolbar.view.*

/*
bug: if an item is selected then unselected and then scrolled - the item is auto-selected
 */
class WordsFragment : BaseFragment() {

    private lateinit var dialogManager: DialogManager
    private lateinit var viewModel: WordsViewModel
    private lateinit var wordsAdapter: WordsAdapter

    private lateinit var llRemoveCancel: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var llSearch: LinearLayout
    private lateinit var editSearchEnDic: EditText
    private lateinit var editSearchIastDic: EditText
    private lateinit var ibSearchClose: ImageButton
    private lateinit var fab: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogManager = DialogManager(requireActivity() as AppCompatActivity)
        return initUI(inflater, container, R.layout.fragment_words)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.Dictionary.CODE_REQUEST_ADD_WORD, Constants.Dictionary.CODE_REQUEST_EDIT_WORD -> {
//                    val word =
//                        data?.getParcelableExtra<Word>(Constants.Dictionary.EXTRA_WORD_RESULT)
                    if (requestCode == Constants.Dictionary.CODE_REQUEST_ADD_WORD) {
                        if (llSearch.visibility == View.VISIBLE) { // the insertion was made from search
                            val filterEn = editSearchEnDic.text.toString()
                            val filterIast = editSearchIastDic.text.toString()
                            viewModel.filter2(filterEn, filterIast)
                                .observe(requireActivity(), { filteredWords ->
                                    wordsAdapter.words = filteredWords
                                })
                        } else {
                            viewModel.getAllWordsCor().observe(requireActivity(), {
                                wordsAdapter.words = it // show all words for now
                            })
                        }
                    } else if (requestCode == Constants.Dictionary.CODE_REQUEST_EDIT_WORD) {
                        if (llSearch.visibility == View.VISIBLE) { // the update was made from search
                            val filterEn = editSearchEnDic.text.toString()
                            val filterIast = editSearchIastDic.text.toString()
                            viewModel.filter2(filterEn, filterIast)
                                .observe(requireActivity(), { filteredWords ->
                                    wordsAdapter.words = filteredWords

                                })
                        } else {
                            viewModel.getAllWordsCor().observe(this@WordsFragment, {
                                wordsAdapter.words = it // show all words for now
                            })
                        }
                    } else {
                        Log.e(LOG_TAG, "success unknown code")
                    }
                }
                Constants.Dictionary.PICKFILE_RESULT_CODE -> {
                    val uri = data?.data
                    Log.d(LOG_TAG, uri?.path.toString())
                    if (uri != null) {
                        viewModel.readFromFiles(uri)
                            .observe(requireActivity(), importObserver)
                    } else {
                        Log.d(LOG_TAG, "path is null")
                    }
                }
                else -> {
                    Log.e(LOG_TAG, "Unknown code on DictionaryActivity.onActivityResult()")
                }
            }
        }
    }

    private fun initUI(
        inflater: LayoutInflater,
        container: ViewGroup?,
        @LayoutRes layoutId: Int
    ): View {

        val root = inflater.inflate(layoutId, container, false)

        // TODO injection
        viewModel = ViewModelProvider(requireActivity()).get(WordsViewModel::class.java)

        // region toolbar
        val toolbar = root.toolbar
        toolbar.title = resources.getString(R.string.dictionary)
        toolbar.toolbarSearch.setOnClickListener {
            if (llSearch.visibility != View.VISIBLE) {
                // show edit with close button
                llSearch.visibility = View.VISIBLE
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

        root.toolbarTitle.text = "Dictionary" // todo resource
        val overflowMenuImageView: ImageView = root.toolbarMenu
        overflowMenuImageView.setOnClickListener { view ->
            val popupMenu = PopupMenu(activity, view)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuItemImport -> {
                        requireActivity().openFilePicker(Constants.Dictionary.PICKFILE_RESULT_CODE)
                        true
                    }
                    R.id.menuItemExport -> {
                        viewModel.getAllWordsCor().observe(requireActivity(), exportObserver)
                        true
                    }
                    R.id.menuItemFind -> {
                        if (llSearch.visibility != View.VISIBLE) {
                            // show edit with close button
                            llSearch.visibility = View.VISIBLE
                            // pop-up IAST keyboard for now
                            // as typing, filter
                            true

                        } else {
                            false
                        }
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }
            popupMenu.inflate(R.menu.menu_dictionary)
            popupMenu.show()
        }
        // endregion

        // search bar
        llSearch = root.llSearchBar
        editSearchEnDic = root.editSearch
        editSearchIastDic = root.editSearchIast
        ibSearchClose = root.btnCloseSearchBar

        editSearchEnDic.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filterEn = s.toString()
                val filterIast = editSearchIastDic.text.toString()
                viewModel.filter2(filterEn, filterIast).observe(requireActivity(), {
                    wordsAdapter.words = it
                })
            }
        })

        editSearchEnDic.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val filterEn = (v as EditText).text.toString()
                val filterIast = editSearchIast.text.toString()
                viewModel.filter2(filterEn, filterIast).observe(requireActivity(), {
                    wordsAdapter.words = it
                })
            }
        }

        editSearchIastDic.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filterEn = editSearchEnDic.text.toString()
                val filterIast = s.toString()
                viewModel.filter2(filterEn, filterIast).observe(requireActivity(), {
                    wordsAdapter.words = it
                })
            }
        })

        editSearchIastDic.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val filterEn = editSearchEnDic.text.toString()
                val filterIast = (v as EditText).text.toString()
                viewModel.filter2(filterEn, filterIast).observe(requireActivity(), {
                    wordsAdapter.words = it
                })
            }
        }

        ibSearchClose.setOnClickListener {
            clearSearch()
            // close the keyboard
            Utils.hideSoftKeyboard(requireActivity())
        }

        // get data from intent
        requireActivity().intent.let { intent ->
            val searchIast = intent.getStringExtra(Constants.Dictionary.EXTRA_WORD_IAST)
            val searchEn = intent.getStringExtra(Constants.Dictionary.EXTRA_WORD_EN)
            if (!searchIast.isNullOrEmpty() || !searchEn.isNullOrEmpty()) {
                if (llSearch.visibility != View.VISIBLE) {
                    // show edit with close button
                    llSearch.visibility = View.VISIBLE
                    if (!searchIast.isNullOrEmpty()) {
                        editSearchIastDic.setText(searchIast)
                    }
                    if (!searchEn.isNullOrEmpty()) {
                        editSearchEnDic.setText(searchEn)
                    }
                    if (searchIast != null) {
                        viewModel.filter(searchIast, true).observe(requireActivity(), { words1 ->
                            if (searchEn != null) {
                                viewModel.filter2(searchEn, searchIast)
                                    .observe(requireActivity(), { words2 ->
                                        wordsAdapter.words = words2
                                    })
                            } else {
                                wordsAdapter.words = words1
                            }
                        })
                    }
                }
            }
        }
        // words recycleview
        recyclerView = root.rv_words
        wordsAdapter = WordsAdapter(
            requireContext(), itemDefinitionClickListener,
            itemEditClickListener,
            longClickListener
        ) {
            llRemoveCancel.visibility = if (it) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = wordsAdapter
        viewModel.getAllWordsCor().observe(requireActivity(), {
            wordsAdapter.words = it
        })

        // remove words
        llRemoveCancel = root.llRemoveCancel
        llRemoveCancel.btnRemove.setOnClickListener(this::removeSelected)
        llRemoveCancel.btnClearSelections.setOnClickListener(this::unselectAll)

        // add fab
        fab = root.fabDictionary
        fab.setOnClickListener {
            val intent = Intent(requireActivity(), AddModifyWordActivity::class.java)
            val word = Word(
                word = "",
                wordIAST = editSearchIast.text.toString(),
                meaningEn = editSearchEnDic.text.toString(),
                meaningRo = ""
            )
            intent.putExtra(Constants.Dictionary.EXTRA_WORD, word)
            intent.putExtra(
                Constants.Dictionary.EXTRA_REQUEST_CODE,
                Constants.Dictionary.CODE_REQUEST_ADD_WORD
            )
            startActivityForResult(intent, Constants.Dictionary.CODE_REQUEST_ADD_WORD)
        }

        return root
    }

    private fun clearSearch() {
        editSearchEnDic.text.clear()
        editSearchIastDic.text.clear()
        llSearch.visibility = View.GONE
    }

    private fun unselectAll(v: View) {
        (recyclerView.adapter as WordsAdapter).unselectRemoveSelected()  // todo fix
        llRemoveCancel.visibility = View.GONE
    }

    @SuppressLint("CheckResult", "RestrictedApi")
    private fun removeSelected(v: View) {
        val adapter = recyclerView.adapter as WordsAdapter
        viewModel.deleteWords(adapter.getWordsToRemove())
            .observe(requireActivity(), {
                adapter.unselectRemoveSelected()
                if (llSearch.visibility != View.VISIBLE) { // show all words
                    wordsAdapter.words = it
                } else { // filter
                    viewModel.filter2(
                        editSearchEnDic.text.toString(),
                        editSearchIastDic.text.toString()
                    ).observe(requireActivity(), { fw ->
                        wordsAdapter.words = fw
                    })
                }
            })
    }

    @SuppressLint("RestrictedApi")
    private fun cancelRemoveSelected() {
        updateRevViewItems(WordsAdapter.TYPE_NON_REMOVABLE)
        llRemoveCancel.visibility = View.GONE
        fab.visibility = View.VISIBLE
    }

    companion object {
        private val LOG_TAG = WordsActivity::class.java.simpleName
    }

    private val exportObserver = Observer<List<Word>> { words ->
        val filename = Constants.Dictionary.FILENAME_WORDS // todo make a constant for now
        if (words != null) {
            viewModel.writeToFiles(Words(words), filename).observe(requireActivity(), {
                requireActivity().export(filename = filename)
            })
        }
    }

    private val importObserver = Observer<List<Word>> {
        wordsAdapter.words = it
    }

    private val itemDefinitionClickListener: View.OnClickListener = View.OnClickListener {
        val word = it.tag as Word
        dialogManager.showWordDialog(word) {
            val intentEdit = Intent(context, AddModifyWordActivity::class.java)
            intentEdit.putExtra(Constants.Dictionary.EXTRA_WORD, word)
            intentEdit.putExtra(
                Constants.Dictionary.EXTRA_REQUEST_CODE,
                Constants.Dictionary.CODE_REQUEST_EDIT_WORD
            )
            startActivityForResult(
                intentEdit,
                Constants.Dictionary.CODE_REQUEST_EDIT_WORD
            )
        }
    }

    private val itemEditClickListener = View.OnClickListener {
        val intentEdit = Intent(requireActivity(), AddModifyWordActivity::class.java)
        intentEdit.putExtra(Constants.Dictionary.EXTRA_WORD, it.tag as Word)
        intentEdit.putExtra(
            Constants.Dictionary.EXTRA_REQUEST_CODE,
            Constants.Dictionary.CODE_REQUEST_EDIT_WORD
        )
        startActivityForResult(
            intentEdit,
            Constants.Dictionary.CODE_REQUEST_EDIT_WORD
        )
    }

    @SuppressLint("RestrictedApi")
    private val longClickListener: View.OnLongClickListener = View.OnLongClickListener {
        updateRevViewItems(WordsAdapter.TYPE_REMOVABLE)
        it.rootView.navigationUp.visibility = View.VISIBLE
        fab.visibility = View.GONE
        true
    }

    private fun updateRevViewItems(type: Int) {
        val adapter: WordsAdapter = recyclerView.adapter as WordsAdapter
        adapter.type = type
        adapter.notifyDataSetChanged()
    }

}