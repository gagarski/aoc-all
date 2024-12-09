package ski.gagar.aoc2024.day3

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2024.day3.part1.processLines
import ski.gagar.aoc2024.day3.part2.processText
import java.io.InputStream

object Day3 : Puzzle {
    override val name = "Mull It Over"
    override val inputPath = "/ski.gagar.aoc.aoc2024.day3/mul.txt"

    override fun part1(input: InputStream): Long =
        input.bufferedReader().useLines { lines ->
            processLines(lines)
        }
    override fun part2(input: InputStream): Long = processText(input.bufferedReader().readTextAndClose())
}