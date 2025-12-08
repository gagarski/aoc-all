package ski.gagar.aoc2025.day1.part2

import ski.gagar.aoc2025.day1.part1.parseStep

private fun zeroClicks(start: Int, step: Int, modulo: Int): Int {
    require(start in 0..<modulo)
    if (step == 0) return 0
    var position = start
    position = (position + step)
    val div = position / modulo

    return when {
        start == 0 && position < 0 -> -div
        position < 0 -> 1 + (-div)
        position == 0 -> 1
        else -> div
    }
}

fun countZeros(lines: Sequence<String>, start: Int = 50, modulo: Int = 100): Int {
    var position = start
    var zeros = 0

    for (line in lines) {
        val step = parseStep(line)
        zeros += zeroClicks(position, step, modulo)
        position = (position + step).mod(modulo)
    }


    return zeros
}