package ski.gagar.aoc2024.day4

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day3.part1.processLines
import ski.gagar.aoc2024.day3.part2.processText
import ski.gagar.aoc2024.day4.part1.countXmas
import ski.gagar.aoc2024.day4.part2.countXMas
import java.io.InputStream

object Day4 : Puzzle {
    override val name = "Ceres Search"
    override val inputPath = "/ski.gagar.aoc.aoc2024.day4/xmas.txt"

    override fun part1(input: InputStream): Int = countXmas(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream): Int = countXMas(input.bufferedReader().lineSequence())
}