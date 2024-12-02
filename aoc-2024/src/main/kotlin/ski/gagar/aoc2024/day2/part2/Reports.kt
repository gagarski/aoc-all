package ski.gagar.aoc2024.day2.part2

import kotlin.math.abs
import kotlin.math.max

fun sign(int: Int) =
    when {
        int < 0 -> -1
        int == 0 -> 0
        else -> 1
    }

fun isSafeStep(current: Int, next: Int?, sign: Int, maxDiff: Int): Boolean {
    if (null == next) return true
    val currentSign = sign(next - current)
    val diff = abs(next - current)
    return currentSign == sign && diff <= maxDiff
}

fun isSafe(level: List<Int>, maxDiff: Int = 3): Boolean {
    if (level.size <= 1) return true
    val sign = sign(level[1] - level[0])

    var removed = false
    var i = 0

    while (i < level.size - 1) {
        val current = level[i]
        val next = level[i + 1]

        when {
            isSafeStep(current, next, sign, maxDiff) -> {
                i++
            }
            !removed -> {
                val nextToNext = level.getOrNull(i + 2)
                if (isSafeStep(current, nextToNext, sign, maxDiff)) {
                    i += 2
                    removed = true
                } else {
                    return false
                }
            }
            else -> return false
        }

    }

    return true
}

fun parseReport(string: String): List<Int> = string.split("""\s+""".toRegex()).map { it.toInt() }

fun countSafe(levels: Sequence<List<Int>>, maxDiff: Int = 3): Int =
    levels.count { isSafe(it, maxDiff) || isSafe(it.reversed(), maxDiff) }

fun countSafeStrings(levels: Sequence<String>, maxDiff: Int = 3) =
    countSafe(levels.map { parseReport(it) }, maxDiff)