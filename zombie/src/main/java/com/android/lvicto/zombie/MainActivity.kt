package com.android.lvicto.zombie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.zombie.coroutines.CoroutinesActivity
import com.android.lvicto.zombie.customview.CustomGroupViewActivity
import com.android.lvicto.zombie.keyboard.activity.CustomKeyboardViewActivity
import com.android.lvicto.zombie.knuth.Expression
import com.android.lvicto.zombie.knuth.Symbol
import com.android.lvicto.zombie.knuth.multiplyPerm
import com.android.lvicto.zombie.livedata.LiveDataTransActivity
import com.android.lvicto.zombie.popup.PopUpAutoDismissActivity
import com.android.lvicto.zombie.popup.PopUpPositioningActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPopUpPositioning.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, PopUpPositioningActivity::class.java))
            }
        }

        btnPopUpAutoDismiss.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, PopUpAutoDismissActivity::class.java))
            }
        }

        btnKeyboardTest.apply {
            setOnClickListener {
            }
        }

        btnCoroutines.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CoroutinesActivity::class.java))
            }
        }

        btnCustomGroupView.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CustomGroupViewActivity::class.java))
            }
        }

        btnCustomKeyboardView.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CustomKeyboardViewActivity::class.java))
            }
        }

        btnLiveDataTransformations.apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, LiveDataTransActivity::class.java))
            }
        }

        val ex1 = Expression(arrayListOf(Symbol("a"), Symbol("c"), Symbol("f"), Symbol("g")))
        val ex2 = Expression(arrayListOf(Symbol("b"), Symbol("c"), Symbol("d")))
        val ex3 = Expression(arrayListOf(Symbol("a"), Symbol("e"), Symbol("d")))
        val ex4 = Expression(arrayListOf(Symbol("f"), Symbol("a"), Symbol("d"), Symbol("e")))
        val ex5 = Expression(arrayListOf(Symbol("b"), Symbol("g"), Symbol("f"), Symbol("a"), Symbol("e")))
        val multExpr = ex1 concat ex2 concat ex3 concat ex4 concat ex5
//        Log.d("Knuth", "${ex1 == ex1}")
        Log.d("Knuth", "$multExpr")
        Log.d("Knuth", "${multExpr.multiplyPerm()}")

    }
}
