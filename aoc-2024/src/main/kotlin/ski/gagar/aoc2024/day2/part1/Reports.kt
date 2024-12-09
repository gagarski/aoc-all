package ski.gagar.aoc2024.day2.part1

import kotlin.math.abs

fun sign(int: Int) =
    when {
        int < 0 -> -1
        int == 0 -> 0
        else -> 1
    }

fun isSafe(report: List<Int>, maxDiff: Int = 3): Boolean {
    if (report.size <= 1) return true
    val sign = sign(report[1] - report[0])

    for (i in 0 until report.size - 1) {
        val currentSign = sign(report[i + 1] - report[i])
        if (currentSign != sign)
            return false

        val diff = abs(report[i + 1] - report[i])
        if (diff > maxDiff)
            return false
    }

    return true
}

fun parseReport(string: String): List<Int> = string.split("""\s+""".toRegex()).map { it.toInt() }

fun countSafe(reports: Sequence<List<Int>>, maxDiff: Int = 3): Int =
    reports.count { isSafe(it, maxDiff) }

fun countSafeStrings(reports: Sequence<String>, maxDiff: Int = 3) =
    countSafe(reports.map { parseReport(it) }, maxDiff)