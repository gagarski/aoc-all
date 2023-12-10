package ski.gagar.aoc2023.day4

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2023.day4.part1.sumPoints
import ski.gagar.aoc2023.day4.part2.processPile
import java.io.InputStream

object Day4 : Puzzle {
    override val name: String = "Scratchcards"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day4/scratchcards.txt"

    override fun part1(input: InputStream) = sumPoints(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) = processPile(input.bufferedReader().lineSequence())
}