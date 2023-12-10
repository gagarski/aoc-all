package ski.gagar.aoc2015.day19

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day19.part1.countReplacement
import ski.gagar.aoc2015.day19.part2.countSteps
import java.io.InputStream

object Day19 : Puzzle {
    override val name: String = "Medicine for Rudolph"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day19/chemistry.txt"

    override fun part1(input: InputStream) =
        countReplacement(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        countSteps(input.bufferedReader().lineSequence())
}