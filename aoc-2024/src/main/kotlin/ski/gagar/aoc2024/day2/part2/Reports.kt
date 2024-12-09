package ski.gagar.aoc2024.day2.part2

import kotlin.math.abs

fun sign(int: Int) =
    when {
        int < 0 -> -1
        int == 0 -> 0
        else -> 1
    }

fun isSafeStep(current: Int?, next: Int, sign: Int, maxDiff: Int): Boolean {
    if (null == current) return true
    val currentSign = sign(next - current)
    val diff = abs(next - current)
    return currentSign == sign && diff <= maxDiff
}

fun isSafe(report: List<Int>, maxDiff: Int = 3): Boolean {
    if (report.size <= 1) return true
    val sign = sign(report[1] - report[0])

    var removed = false

    var prev: Int? = null

    for (level in report) {
        val isSafe = isSafeStep(prev, level, sign, maxDiff)

        when {
            isSafe -> {
                prev = level
                continue
            }
            !removed && !isSafe -> {
                removed = true
                continue
            }
            else -> {
                return false
            }

        }
    }

    return true
}

fun parseReport(string: String): List<Int> = string.split("""\s+""".toRegex()).map { it.toInt() }

fun countSafe(reports: Sequence<List<Int>>, maxDiff: Int = 3): Int =
    reports.count { isSafe(it, maxDiff) || isSafe(it.reversed(), maxDiff) }

fun countSafeStrings(reports: Sequence<String>, maxDiff: Int = 3) =
    countSafe(reports.map { parseReport(it) }, maxDiff)