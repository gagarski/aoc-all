package ski.gagar.aoc2022.day4.part1

import ski.gagar.aoc.util.getResourceAsStream

fun parseIntRange(str: String): IntRange {
    val parts = str.split("-")
    require(parts.size == 2) {
        "Range should be two numbers separated by dash"
    }

    return parts[0].toInt()..parts[1].toInt()
}

fun parsePairOfRanges(str: String): Pair<IntRange, IntRange> {
    val parts = str.split(",")
    require(parts.size == 2) {
        "Pair should be two ranges separated by comma"
    }

    return parseIntRange(parts[0]) to parseIntRange(parts[1])
}

operator fun IntRange.contains(other: IntRange): Boolean =
    other.first in this && other.last in this

fun Pair<IntRange, IntRange>.fullyIntersect(): Boolean {
    val (first, second) = this

    return first in second || second in first
}

fun countFullyIntersecting(strings: Sequence<String>) =
    strings.map { parsePairOfRanges(it) }.count { it.fullyIntersect() }

fun day4Part1() {
    println("day4/part1/compartments: ${
        countFullyIntersecting(getResourceAsStream("/ski.gagar.aoc.aoc2022.day4/compartments.txt").bufferedReader().lineSequence())
    }")
}
