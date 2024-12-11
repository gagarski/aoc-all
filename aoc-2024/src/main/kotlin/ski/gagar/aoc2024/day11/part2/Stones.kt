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
    private val cache: MutableMap<BigInteger, NavigableMap<Int, PersistentList<BigInteger>>> = mutableMapOf()

    fun advanceCache(stone: BigInteger, stones: List<BigInteger>, stepsToAdd: Int) {
        val entry = cache[stone] ?: TreeMap()

        outer@ for (i in 1 until MAX_DEPTH - stepsToAdd) {
            var newStones = persistentListOf<BigInteger>()

            for (otherStone in stones) {
                val cached = cache[otherStone]?.get(i) ?: continue@outer
                newStones = newStones.addAll(cached)
            }
            if (newStones.isEmpty()) {
                continue
            }
            entry[stepsToAdd + i] = newStones
        }
        cache[stone] = entry

    }

    private fun countStones(seed: BigInteger,
                            steps: Int): BigInteger {
        if (steps == 0) {
            return BigInteger.ONE
        }
        val cacheEntry = cache[seed]?.floorEntry(steps)
        if (cacheEntry != null) {
            val remSteps = steps - cacheEntry.key
            val stones = cacheEntry.value
            var count = BigInteger.ZERO


            for (stone in stones) {
                count += countStones(stone, remSteps)
            }

            advanceCache(seed, stones, cacheEntry.key)
            if (steps > 40)
                println("Ret from $steps")
            return count
        } else {
            val stones = processStone(seed).toPersistentList()

            val newItem = TreeMap<Int, PersistentList<BigInteger>>()
            newItem[1] = stones
            cache[seed] = newItem

            var count = BigInteger.ZERO

            for (stone in stones) {
                count += countStones(stone, steps - 1)
            }

            advanceCache(seed, stones, 1)
            return count
        }
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
        eval.countStones(it, 35)
        println("warmed up")
        eval.countStones(it, n)
    }
