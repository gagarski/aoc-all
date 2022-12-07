package ski.gagar.aoc2015.day1.part2

import ski.gagar.aoc.util.getResourceAsStream

const val BASEMENT = -1

fun floors(input: String, target: Int = BASEMENT): Int {
    var floor = 0

    for ((index, instruction) in input.withIndex()) {
        when (instruction) {
            '(' -> floor++
            ')' -> floor--
            else -> {} // brainfuck way!
        }

        if (floor == target) {
            return index + 1
        }
    }
    return -1
}

fun day1Part2() {
    println(
        "day1/part2/floors: ${
            floors(getResourceAsStream("/ski.gagar.aoc.aoc2015.day1/floors.txt").bufferedReader().lineSequence().first())
        }"
    )
}
