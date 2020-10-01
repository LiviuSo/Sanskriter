package com.android.lvicto.zombie.coroutines.retailxsimu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.coroutines.retailxsimu.data.StlResult
import com.android.lvicto.zombie.coroutines.retailxsimu.adapter.StlResultsAdapter
import com.android.lvicto.zombie.coroutines.retailxsimu.viewmodel.StlResultsViewModel
import kotlinx.android.synthetic.main.activity_stl_results.*

class StlResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val stlResultsViewModel = StlResultsViewModel() // todo inject with Koin

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stl_results)

        val results = stlResultsViewModel.getStlResults()
//        val results = stlResultsViewModel.getStlResults2() // to test the speed
        val adapter = StlResultsAdapter(this)
        stlResultsRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        stlResultsRecView.adapter = adapter
        (stlResultsRecView.adapter as StlResultsAdapter).results = results

    }
}