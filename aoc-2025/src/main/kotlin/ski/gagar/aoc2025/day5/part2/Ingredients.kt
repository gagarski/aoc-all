package ski.gagar.aoc2025.day5.part2

import ski.gagar.aoc2025.day5.part1.toBigIntRange
import java.math.BigInteger


fun <T : Comparable<T>> tryMerge(first: ClosedRange<T>, second: ClosedRange<T>): ClosedRange<T>? {
    if (first.endInclusive < second.start) return null
    if (second.endInclusive < first.start) return null
    return minOf(first.start, second.start)..maxOf(first.endInclusive, second.endInclusive)
}

fun mergeRanges(set: Set<ClosedRange<BigInteger>>): Set<ClosedRange<BigInteger>> {
    val res = mutableSetOf<ClosedRange<BigInteger>>()
    val merged = mutableSetOf<Int>()
    val list = set.toList().sortedBy { it.start } // sorting will help to not look back
    for (i in 0..<list.size) {
        var range = list[i]
        if (i in merged) continue
        for (j in i..<list.size) {
            if (j in merged) continue
            val second = list[j]

            tryMerge(range, second)?.let {
                range = it
                merged.add(j)
            }
        }
        res.add(range)
    }
    return res
}

fun countAllFreshIngredients(lines: Sequence<String>): BigInteger =
    lines.takeWhile { it.isNotEmpty() }
        .map {
            it.toBigIntRange()
        }.toSet()
        .let {
            mergeRanges(it)
        }.sumOf {
            it.endInclusive - it.start + BigInteger.ONE
        }
