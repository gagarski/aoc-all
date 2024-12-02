package ski.gagar.aoc2024.day2

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day2 : Puzzle {
    override val name = "Red-Nosed Reports"
    override val inputPath = "/ski.gagar.aoc.aoc2024.day2/reports.txt"

    override fun part1(input: InputStream): Int =
        ski.gagar.aoc2024.day2.part1.countSafeStrings(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream): Int =
        ski.gagar.aoc2024.day2.part2.countSafeStrings(input.bufferedReader().lineSequence())
}