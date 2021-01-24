package com.android.lvicto.sanskriter.ui.fragments

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.android.lvicto.db.entity.Word
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.util.AssetsHelper
import com.android.lvicto.ui.AddModifyWordActivity
import com.android.lvicto.util.Constants
import kotlinx.android.synthetic.main.fragment_zoom_page.*
import kotlinx.android.synthetic.main.fragment_zoom_page.btnAddDic
import kotlinx.android.synthetic.main.fragment_zoom_page.view.*
import kotlinx.android.synthetic.main.search_bar.*
import kotlinx.android.synthetic.main.view_add_words.*
import kotlinx.android.synthetic.main.view_add_words.view.*
import java.util.zip.Inflater

class ZoomPageFragment : Fragment() {

    private lateinit var asset: String
    private lateinit var viewSearchDic: View
    private lateinit var viewParent: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewParent = inflater.inflate(R.layout.fragment_zoom_page, container, false)
        AssetsHelper.loadAsset(viewParent.findViewById(R.id.zoomablePage), asset)

        viewSearchDic = inflater.inflate(R.layout.view_add_words, container, false)

        viewSearchDic.btnAddWord.setOnClickListener {
            val intent = Intent(activity, AddModifyWordActivity::class.java)
            val word = Word(word = "",
                    wordIAST = viewSearchDic.edWordIast.text.toString(),
                    meaningEn = viewSearchDic.edWordEn.text.toString(),
                    meaningRo = "")
            intent.putExtra(Constants.Dictionary.EXTRA_WORD, word)
            intent.putExtra(Constants.Dictionary.EXTRA_REQUEST_CODE, Constants.Dictionary.CODE_REQUEST_ADD_WORD)
            startActivity(intent)
            bottomHolder.removeView(viewSearchDic)
            llBottomToolbar.visibility = View.VISIBLE
        }

        viewSearchDic.btnAddWordCancel.setOnClickListener {
            bottomHolder.removeView(viewSearchDic)
            llBottomToolbar.visibility = View.VISIBLE
        }

        viewParent.btnAddDic.setOnClickListener {
            if (viewParent.findViewById<ConstraintLayout>(R.id.addWordsContainer) == null) {
                viewParent.bottomHolder.addView(viewSearchDic)
            }
            llBottomToolbar.visibility = View.GONE
        }

        return viewParent
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
