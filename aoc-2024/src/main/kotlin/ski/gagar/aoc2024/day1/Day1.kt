package ski.gagar.aoc2024.day1

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day1.part1.diffSumParse
import ski.gagar.aoc2024.day1.part2.similarityScore
import java.io.InputStream

object Day1 : Puzzle {
    override val name = "Historian Hysteria"
    override val inputPath: String = "/ski.gagar.aoc.aoc2024.day1/locations.txt"

    override fun part1(input: InputStream): Int = diffSumParse(input.bufferedReader().lineSequence())
    override fun part2(input: InputStream): Int = similarityScore(input.bufferedReader().lineSequence())
}