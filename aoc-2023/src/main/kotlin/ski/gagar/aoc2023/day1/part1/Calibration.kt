package ski.gagar.aoc2023.day1.part1

import ski.gagar.aoc.util.getResourceAsStream

private fun Char.digitToInt(): Int {
    require(isDigit())
    return this - '0'
}

private val String.calibrationValue
    get() = this.first { it.isDigit() }.digitToInt() * 10 + this.last { it.isDigit() }.digitToInt()

fun calibration(strings: Sequence<String>) = strings.sumOf { it.calibrationValue }
