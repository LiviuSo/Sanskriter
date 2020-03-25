package com.android.lvicto.zombie.livedata

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.lvicto.zombie.R

class LiveDataTransActivity : AppCompatActivity() {

    private val viewModel = ProductViewModel()
    private lateinit var outputProduct: TextView
    private lateinit var outputAvailability: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data_trans)

        initViewModel()

        initUI()

        initObservables()

    }

    private fun initViewModel() {
        lifecycle.addObserver(viewModel)
    }

    private fun initObservables() {
        viewModel.initProduct()
        viewModel.product.observe(this, Observer {
            outputProduct.text = it?.toString() ?: "Product not found."
        })

        viewModel.initAvailability()
        viewModel.availability.observe(this, Observer {
            outputAvailability.text = if (it) {
                "Available"
            } else {
                "Not available"
            }
        })
    }

    private fun initUI() {
        outputProduct = findViewById(R.id.tvProduct)
        outputAvailability = findViewById(R.id.tvProductAvailability)

        val etStyle = findViewById<TextView>(R.id.etStyleNumber)
        findViewById<Button>(R.id.btnSearchByStyle).apply {
            setOnClickListener {
                with(etStyle.text) {
                    if (!this.isNullOrEmpty()) {
                        viewModel.styleColor.value = this.toString()
                    } else {
                        Toast.makeText(this@LiveDataTransActivity, "Style color must not be empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val etPid = findViewById<TextView>(R.id.etPid)
        findViewById<Button>(R.id.btnSearchByPid).apply {
            setOnClickListener {
                with(etPid.text) {
                    if (!this.isNullOrEmpty()) {
                        viewModel.pid.value = this.toString()
                    } else {
                        Toast.makeText(this@LiveDataTransActivity, "Pid must not be empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val storeId = findViewById<EditText>(R.id.etStoreId)
        findViewById<Button>(R.id.btnAvailableInStore).apply {
            setOnClickListener {
                with(storeId.text.toString()) {
                    if(this.isNotEmpty()) {
                        Toast.makeText(this@LiveDataTransActivity, "StoreId = $this", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LiveDataTransActivity, "StoreId must not be empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}
