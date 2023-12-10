package ski.gagar.aoc2022.day4

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day4.part1.countFullyIntersecting
import ski.gagar.aoc2022.day4.part2.countIntersecting
import java.io.InputStream

object Day4 : Puzzle {
    override val name: String = "Camp Cleanup"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day4/compartments.txt"

    override fun part1(input: InputStream) = countFullyIntersecting(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) = countIntersecting(input.bufferedReader().lineSequence())
}