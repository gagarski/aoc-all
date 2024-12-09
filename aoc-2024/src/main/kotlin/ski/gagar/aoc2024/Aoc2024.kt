package ski.gagar.aoc2024

import ski.gagar.aoc.util.Contest
import ski.gagar.aoc2024.day1.Day1
import ski.gagar.aoc2024.day2.Day2
import ski.gagar.aoc2024.day3.Day3
import ski.gagar.aoc2024.day4.Day4
import ski.gagar.aoc2024.day5.Day5
import ski.gagar.aoc2024.day6.Day6
import ski.gagar.aoc2024.day7.Day7
import ski.gagar.aoc2024.day8.Day8
import ski.gagar.aoc2024.day9.Day9

object Aoc2024 : Contest {
    override val puzzles = listOf(
        Day1,
        Day2,
        Day3,
        Day4,
        Day5,
        Day6,
        Day7,
        Day8,
        Day9
    )
}

fun main() {
    Aoc2024.run()
}