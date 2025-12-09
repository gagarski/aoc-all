package ski.gagar.aoc2025

import ski.gagar.aoc.util.Contest
import ski.gagar.aoc2025.day1.Day1
import ski.gagar.aoc2025.day2.Day2
import ski.gagar.aoc2025.day3.Day3
import ski.gagar.aoc2025.day4.Day4
import ski.gagar.aoc2025.day5.Day5
import ski.gagar.aoc2025.day6.Day6
import ski.gagar.aoc2025.day7.Day7
import ski.gagar.aoc2025.day8.Day8
import ski.gagar.aoc2025.day9.Day9
import kotlin.io.path.Path

object Aoc2025 : Contest {
    override val puzzles = listOf(
//        Day1,
//        Day2,
//        Day3,
//        Day4,
//        Day5,
//        Day6,
//        Day7,
//        Day8,
        Day9
    )
}

fun main(args: Array<String>) {
    require(args.size == 1) {
        "This app requires exactly one arg (base input folder)"
    }
    Aoc2025.run(Path(args[0]))
}