package ski.gagar.aoc2024.day11.part2

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
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

fun processStone(stone: BigInteger): Sequence<BigInteger> = sequence {
    val splitted = stone.split()
    when {
        stone == BigInteger.ZERO -> yield(BigInteger.ONE)
        splitted != null -> {
            yield(splitted.first)
            yield(splitted.second)
        }
        else -> {
            yield(stone * BI_2024)
        }

    }
}


class StonesEval {
    private val cache: MutableMap<Pair<BigInteger, Int>, BigInteger> = mutableMapOf()


    private fun countStones(seed: BigInteger,
                            steps: Int): BigInteger {
        if (steps == 0) {
            return BigInteger.ONE
        }

        val cached = cache[Pair(seed, steps)]

        if (cached != null) {
            return cached
        }

        val stones = processStone(seed).toPersistentList()

        val newItem = TreeMap<Int, PersistentList<BigInteger>>()
        newItem[1] = stones

        var count = BigInteger.ZERO

        for (stone in stones) {
            count += countStones(stone, steps - 1)
        }

        cache[Pair(seed, steps)] = count
        return count
    }

    fun countStones(stones: List<BigInteger>, steps: Int = 75): BigInteger {
        var res = BigInteger.ZERO
        for (count in stones.map { countStones(it, steps) }) {
            res += count
        }

        return res
    }

    companion object {
        const val MAX_DEPTH = 5
    }
}

fun countStones(string: String, n: Int = 75) =
    string.split(" ").map { it.toBigInteger() }.let {
        val eval = StonesEval()
        eval.countStones(it, n)
    }
