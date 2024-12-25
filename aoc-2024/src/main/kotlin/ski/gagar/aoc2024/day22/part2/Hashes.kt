package ski.gagar.aoc2024.day22.part2

import ski.gagar.aoc2024.day22.part1.randomSeq

fun Long.price() = (this % 10).toInt()

fun Long.priceSeq() = randomSeq().map { it.price() }

data class CacheKey(val seed: Long, val diffs: List<Int>)

fun Long.cacheDiffs(limit: Int = 2000, size: Int = 4, cache: MutableMap<CacheKey, Int>) {
    val prices = ArrayDeque<Int>()

    fun processPrice(price: Int) {
        prices.addLast(price)
        if (prices.size > size + 1) {
            prices.removeFirst()
        }
    }

    fun currentDiffs() = prices.asSequence().zipWithNext { current, next -> next - current }.toList()

    for (p in priceSeq().take(limit)) {
        processPrice(p)

        if (prices.size == size + 1) {
            val current = currentDiffs()
            cache.putIfAbsent(CacheKey(this, current), p)
        }
    }
}

fun bestGain(input: Sequence<String>, limit: Int = 2000, size: Int = 4): Int {
    val cache = mutableMapOf<CacheKey, Int>()
    val seeds = input.map { it.toLong() }.toList()

    for (seed in seeds) {
        seed.cacheDiffs(limit, size, cache)
    }

    val diffs = cache.keys.asSequence().map { it.diffs }.toSet()

    var bestSoFar = 0

    for (diff in diffs) {
        val current = seeds.sumOf { cache[CacheKey(it, diff)] ?: 0 }

        if (current > bestSoFar) {
            bestSoFar = current
        }
    }

    return bestSoFar
}