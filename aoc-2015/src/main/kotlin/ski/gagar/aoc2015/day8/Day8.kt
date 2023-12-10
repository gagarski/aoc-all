package ski.gagar.aoc2015.day8

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day8 : Puzzle {
    override val name: String = "Matchsticks"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day8/escaping.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day8.part1.countDiff(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day8.part2.countDiff(input.bufferedReader().lineSequence())
}