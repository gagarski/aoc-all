package ski.gagar.aoc2025.day3.part1

import java.math.BigInteger

private fun <T, S : Comparable<S>> Iterable<T>.maxOfRemaining(stash: Int, drop: Int = 0, by: (T) -> S) =
    this.asSequence().drop(drop).toList().dropLast(stash).maxBy(by)

private fun List<Int>.combineJoltage() = this.joinToString("").toBigInteger()

private fun List<Int>.maxJoltage(count: Int = 2): BigInteger {
    val maxValues = mutableListOf<Int>()
    var curMaxIndex = -1
    val withIndex = this.withIndex().toList()
    for (i in 0..<count) {
        val max = withIndex.maxOfRemaining(count - i - 1, curMaxIndex + 1) { it.value}
        curMaxIndex = max.index
        maxValues.add(max.value)
    }
    return maxValues.combineJoltage()
}

private fun parseLine(line: String) = line.map { it - '0' }

fun sumMaxJoltages(lines: Sequence<String>, count: Int = 2) =
    lines.map {
        parseLine(it).maxJoltage(count)
    }.fold(BigInteger.ZERO) { a, b -> a + b }
