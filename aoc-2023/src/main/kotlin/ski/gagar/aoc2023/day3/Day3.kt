package ski.gagar.aoc2023.day3

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day3.part1.partNumbers
import ski.gagar.aoc2023.day3.part2.gearRatio
import java.io.InputStream

object Day3 : Puzzle {
    override val name: String = "Gear Ratios"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day3/board.txt"

    override fun part1(input: InputStream) = partNumbers(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) = gearRatio(input.bufferedReader().lineSequence())
}