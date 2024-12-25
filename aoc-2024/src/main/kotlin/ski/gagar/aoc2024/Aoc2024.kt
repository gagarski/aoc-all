package ski.gagar.aoc2024

import ski.gagar.aoc.util.Contest
import ski.gagar.aoc2024.day1.Day1
import ski.gagar.aoc2024.day10.Day10
import ski.gagar.aoc2024.day11.Day11
import ski.gagar.aoc2024.day12.Day12
import ski.gagar.aoc2024.day13.Day13
import ski.gagar.aoc2024.day14.Day14
import ski.gagar.aoc2024.day15.Day15
import ski.gagar.aoc2024.day16.Day16
import ski.gagar.aoc2024.day17.Day17
import ski.gagar.aoc2024.day18.Day18
import ski.gagar.aoc2024.day19.Day19
import ski.gagar.aoc2024.day2.Day2
import ski.gagar.aoc2024.day20.Day20
import ski.gagar.aoc2024.day21.Day21
import ski.gagar.aoc2024.day22.Day22
import ski.gagar.aoc2024.day23.Day23
import ski.gagar.aoc2024.day24.Day24
import ski.gagar.aoc2024.day3.Day3
import ski.gagar.aoc2024.day4.Day4
import ski.gagar.aoc2024.day5.Day5
import ski.gagar.aoc2024.day6.Day6
import ski.gagar.aoc2024.day7.Day7
import ski.gagar.aoc2024.day8.Day8
import ski.gagar.aoc2024.day9.Day9
import kotlin.io.path.Path

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
        Day9,
        Day10,
        Day11,
        Day12,
        Day13,
        Day14,
        Day15,
        Day16,
        Day17,
        Day18,
        Day19,
        Day20,
        Day21,
        Day22,
        Day23,
        Day24
    )
}

fun main(args: Array<String>) {
    require(args.size == 1) {
        "This app requires exactly one arg (base input folder)"
    }
    Aoc2024.run(Path(args[0]))
}