package ski.gagar.aoc2024.day1

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2024.day1.part1.diffSumParse
import ski.gagar.aoc2024.day1.part2.similarityScore
import java.io.InputStream

object Day1 : Puzzle {
    override val name = "Historian Hysteria"

    override fun part1(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            diffSumParse(lines)
        }

    override fun part2(input: InputStream): Int =
        input.bufferedReader().useLines { lines ->
            similarityScore(lines)
        }
}