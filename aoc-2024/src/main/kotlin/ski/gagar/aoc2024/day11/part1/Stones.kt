package ski.gagar.aoc2024.day11.part1

import java.math.BigInteger
import java.util.*


val BigInteger.nDigits: Int
    get() {
        var n = 0
        var curr = this

        do {
            n++
            curr /= BigInteger.TEN
        } while (curr != BigInteger.ZERO)

        return n
    }

fun BigInteger.split(): Pair<BigInteger, BigInteger>? {
    val nDigits = this.nDigits
    if (nDigits % 2 != 0) return null

    val splitter = BigInteger.TEN.pow(nDigits / 2)

    return this / splitter to this % splitter
}

private val BI_2024 = BigInteger("2024")

fun MutableList<BigInteger>.stonesIter() {
    val itr = listIterator()
    while (itr.hasNext()) {
        val currrent = itr.next()
        val splitted = currrent.split()
        when {
            currrent == BigInteger.ZERO -> {
                itr.remove()
                itr.add(BigInteger.ONE)
            }
            splitted != null -> {
                itr.remove()
                itr.add(splitted.first)
                itr.add(splitted.second)
            }
            else -> {
                itr.remove()
                itr.add(currrent * BI_2024)
            }
        }
    }
}

fun MutableList<BigInteger>.stoneIters(n: Int = 25) {
    for (index in 0 until n) {
        stonesIter()
    }
}

fun countStones(string: String, n: Int = 25) =
    string.split(" ").map { it.toBigInteger() }.toCollection(LinkedList()).apply { stoneIters(n) }.size
