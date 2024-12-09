package ski.gagar.aoc2015.day19

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day19.part1.countReplacement
import ski.gagar.aoc2015.day19.part2.countSteps
import java.io.InputStream

object Day19 : Puzzle {
    override val name: String = "Medicine for Rudolph"

    override fun part1(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            countReplacement(lines)
        }

    override fun part2(input: InputStream) =
        input.bufferedReader().useLines { lines ->
            countSteps(lines)
        }
}