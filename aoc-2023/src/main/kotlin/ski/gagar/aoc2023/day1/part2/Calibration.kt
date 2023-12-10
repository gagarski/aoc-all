package ski.gagar.aoc2023.day1.part2

import ski.gagar.aoc.util.getResourceAsStream

private val DIGITS = mapOf(
    "1" to 1,
    "2" to 2,
    "3" to 3,
    "4" to 4,
    "5" to 5,
    "6" to 6,
    "7" to 7,
    "8" to 8,
    "9" to 9,
    "0" to 0,
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
    "zero" to 0
)

fun String.firstDigit(): Int =
    DIGITS.asSequence().map {
        this.indexOf(it.key) to it.value
    }.filter { (k, _) ->
        k >= 0
    }.minBy { (k, _) ->
        k
    }.second

fun String.lastDigit(): Int =
    DIGITS.asSequence().map {
        this.lastIndexOf(it.key) to it.value
    }.filter { (k, _) ->
        k >= 0
    }.maxBy { (k, _) ->
        k
    }.second

private val String.calibrationValue
    get() = this.firstDigit() * 10 + this.lastDigit()

fun calibration(strings: Sequence<String>) = strings.sumOf {
    it.calibrationValue
}