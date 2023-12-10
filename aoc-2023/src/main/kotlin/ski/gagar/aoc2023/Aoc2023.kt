package ski.gagar.aoc2023

import ski.gagar.aoc.util.Contest
import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day1.Day1
import ski.gagar.aoc2023.day2.Day2
import ski.gagar.aoc2023.day3.Day3
import ski.gagar.aoc2023.day4.Day4
import ski.gagar.aoc2023.day5.Day5
import ski.gagar.aoc2023.day6.Day6
import ski.gagar.aoc2023.day7.Day7
import ski.gagar.aoc2023.day8.Day8
import ski.gagar.aoc2023.day9.Day9

object Aoc2023 : Contest {
    override val puzzles: List<Puzzle> =
        listOf(
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
    Aoc2023.run()
}