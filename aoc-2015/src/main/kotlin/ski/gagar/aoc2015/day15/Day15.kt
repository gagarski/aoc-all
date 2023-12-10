package ski.gagar.aoc2015.day15

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day15 : Puzzle {
    override val name: String = "Science for Hungry People"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day15/cookies.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day15.part1.getOptimalCompoundValue(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day15.part2.getOptimalCompoundValue(input.bufferedReader().lineSequence())
}