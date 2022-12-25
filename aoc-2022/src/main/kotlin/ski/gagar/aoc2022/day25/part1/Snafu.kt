package ski.gagar.aoc2022.day25.part1

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day24.part2.findQuickestLength
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

fun String.snafuToLong(): Long {
    var res = 0L
    var currentBase = 1L

    for (char in this.reversed()) {
        val digit = when (char) {
            '-' -> -1
            '=' -> -2
            in '0'..'2' -> char - '0'
            else -> throw IllegalArgumentException("$this is not a valid snafu number")
        }
        res += digit * currentBase
        currentBase *= 5
    }
    return res
}


fun Long.toSnafu(): String = buildString {
    var current = this@toSnafu
    var flag = false

    while (current != 0L) {
        val digit = current % 5
        val withFlag = if (flag) digit + 1 else digit
        val moveFlagToNext = withFlag == 5L
        val corrected = withFlag % 5


        val digitWithNeg = when {
            corrected < 3 -> corrected
            else -> -(5 - corrected)
        }

        append(
            when (digitWithNeg) {
                -1L -> "-"
                -2L -> "="
                in 0..2 -> "$digitWithNeg"
                else -> throw IllegalStateException("Should not happen")
            }
        )

        flag = digitWithNeg < 0 || moveFlagToNext
        current /= 5
    }

    if (flag) {
        append("1")
    }
}.reversed()


fun genSnafuSum(lines: Sequence<String>) = lines.map { it.snafuToLong() }.sum().toSnafu()

fun day25Part1() {
    println("day25/part1/snafu: \n${
        genSnafuSum(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day25/snafu.txt").bufferedReader().lineSequence()
        )
    }")
}
