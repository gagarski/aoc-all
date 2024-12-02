package ski.gagar.aoc2024.day2.part1

import kotlin.math.abs

fun sign(int: Int) =
    when {
        int < 0 -> -1
        int == 0 -> 0
        else -> 1
    }

fun isSafe(level: List<Int>, maxDiff: Int = 3): Boolean {
    if (level.size <= 1) return true
    val sign = sign(level[1] - level[0])

    for (i in 0 until level.size - 1) {
        val currentSign = sign(level[i + 1] - level[i])
        if (currentSign != sign)
            return false

        val diff = abs(level[i + 1] - level[i])
        if (diff > maxDiff)
            return false
    }

    return true
}

fun parseReport(string: String): List<Int> = string.split("""\s+""".toRegex()).map { it.toInt() }

fun countSafe(levels: Sequence<List<Int>>, maxDiff: Int = 3): Int =
    levels.count { isSafe(it, maxDiff) }

fun countSafeStrings(levels: Sequence<String>, maxDiff: Int = 3) =
    countSafe(levels.map { parseReport(it) }, maxDiff)