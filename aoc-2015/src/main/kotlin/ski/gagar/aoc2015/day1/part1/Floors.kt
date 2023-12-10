package ski.gagar.aoc2015.day1.part1

import ski.gagar.aoc.util.getResourceAsStream


fun floors(input: String): Int {
    var floor = 0

    for (instruction in input) {
        when (instruction) {
            '(' -> floor++
            ')' -> floor--
            else -> {} // brainfuck way!
        }
    }
    return floor
}