package com.android.lvicto.sanskriter.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.lvicto.sanskriter.R
import com.android.lvicto.sanskriter.utils.AssetsHelper

class ZoomPageFragment : Fragment() {

    lateinit var asset: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_zoom_page, container, false)
        AssetsHelper.loadAsset(view.findViewById(R.id.zoomablePage), asset)
        return view
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
