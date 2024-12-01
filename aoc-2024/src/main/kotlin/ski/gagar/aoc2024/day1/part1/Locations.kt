package ski.gagar.aoc2024.day1.part1

import kotlin.math.abs

fun diffSum(nums: Sequence<Pair<Int, Int>>) =
    nums.map { abs(it.first - it.second) }.sum()

fun parseLine(line: String): Pair<Int, Int> {
    val parts = line.split("""\s+""".toRegex())
    require(parts.size == 2)
    return Pair(parts[0].toInt(), parts[1].toInt())
}

fun diffSumParse(lines: Sequence<String>): Int {
    val pairs = lines.map { parseLine(it) }.toList()
    val first = pairs.asSequence().map { it.first }.sorted()
    val second = pairs.asSequence().map { it.second }.sorted()
    return diffSum(first.zip(second))
}