package com.android.lvicto.zombie.knuth

import android.util.Log
import com.android.lvicto.zombie.knuth.Symbol.Companion.CLOSE_PAR
import com.android.lvicto.zombie.knuth.Symbol.Companion.NO_SYMBOL
import com.android.lvicto.zombie.knuth.Symbol.Companion.OPEN_PAR
import java.util.*


/* Knuth permutation multiplication */

class Symbol(var label: String, var isMarked: Boolean = false) {
    companion object {
        val OPEN_PAR = Symbol("(")
        val CLOSE_PAR = Symbol(")")
        val NO_SYMBOL = Symbol("")
    }

    fun mark() {
        isMarked = true
    }

    fun replace(s: Symbol) {
        label = StringBuffer(s.label).toString()
        isMarked = s.isMarked
    }

    override fun toString(): String {
        return StringBuffer(this.label).apply {
            this.append(if (isMarked) {
                "."
            } else {
                ""
            })
        }.toString()
    }

    override fun equals(other: Any?): Boolean {
        return label == (other as? Symbol)?.label
    }

    override fun hashCode(): Int {
        var result = label.hashCode()
        result = 31 * result + isMarked.hashCode()
        return result
    }

    fun unmark() {
        isMarked = false
    }
}

class Expression(sym: List<Symbol>) {

    constructor() : this(arrayListOf())

    internal var symbols: ArrayList<Symbol> = arrayListOf()

    init {
        symbols.add(0, Symbol.OPEN_PAR)
        symbols.addAll(sym)
        symbols.add(Symbol.CLOSE_PAR)
    }

    override fun toString(): String {
        return StringBuffer().apply {
            symbols.forEach {
                this.append(it.toString())
                this.append(" ")
            }
        }.toString()
    }

    infix fun concat(expression: Expression): Expression {
        return this.apply {
            symbols.addAll(expression.symbols)
        }
    }

    fun copy(): Expression {
        return Expression().also {
            it.symbols.clear()
            it.symbols.addAll(this.symbols.map { s ->
                Symbol(s.label, s.isMarked)
            })
        }
    }
}

/**
 * Assuming the expression is written as a multiplication of permutations:
 * for eg: ( a c f g ) ( b c d ) ( a e d ) ( f a d e ) ( b g f a e )
 * transform the expression into the multiplication of the permutations
 */
fun Expression.multiplyPerm(trace: Boolean = false): Expression {
    val sym = this.copy().symbols
    val size = sym.size
    val outputSym = arrayListOf<Symbol>()

    fun output(s: Symbol) {
        if(trace) {
            Log.d("Knuth", "$s")
        }
        outputSym.add(Symbol(s.label))
    }

    fun outputAll() {
        Log.d("Knuth", "${Expression(sym)}")
    }

    fun firstPass() {
        val nextSymAfterOpenPar = NO_SYMBOL
        (0 until size).forEach {
            if (sym[it] == OPEN_PAR) {
                sym[it].mark()
                nextSymAfterOpenPar.replace(sym[it + 1])
            } else if (sym[it] == CLOSE_PAR) {
                sym[it].replace(nextSymAfterOpenPar)
                sym[it].mark()
            }
        }
    }

    fun findFirstUnmarked(start: Int): Int {
        var index = start
        while (index < size && sym[index].isMarked) index++
        return index
    }

    fun findNextOccurrence(startingIndex: Int, current: Int): Int {
        var index = startingIndex + 1
        while (index < size && sym[index] != sym[current]) index++
        return index
    }

    fun completeCycle(start: Int) {
        var current = start + 1
        var startingIndex = current + 1
        do {
            val next = findNextOccurrence(startingIndex, current)
            if (next < size) { // found
                sym[next].mark()
                current = next + 1
                startingIndex = current

            } else { // found an elem
                if (sym[start] != sym[current]) {
                    output(sym[current])
                    startingIndex = 0 // restart from the beginning
                } else {
                    break
                }
            }
        } while (true)
    }

    firstPass()
    outputAll()
    var start = 1
    do {
        start = findFirstUnmarked(start)
        if ((start == size)) break // done
        sym[start].mark()
        output(OPEN_PAR)
        output(sym[start])
        completeCycle(start)
        output(CLOSE_PAR)
    } while (true)

    return Expression().apply {
        this.symbols = outputSym
    }
}