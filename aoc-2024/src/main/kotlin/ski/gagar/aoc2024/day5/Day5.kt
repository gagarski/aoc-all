package ski.gagar.aoc2024.day5

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day5.part1.sumValidMiddlePages
import ski.gagar.aoc2024.day5.part2.sumInvalidMiddlePages
import java.io.InputStream

object Day5 : Puzzle {
    override val name = "Print Queue"
    override val inputPath = "/ski.gagar.aoc.aoc2024.day5/pages.txt"

    override fun part1(input: InputStream): Int = sumValidMiddlePages(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream): Int = sumInvalidMiddlePages(input.bufferedReader().lineSequence())
}