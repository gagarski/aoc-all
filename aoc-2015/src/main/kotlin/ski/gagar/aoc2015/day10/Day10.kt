package ski.gagar.aoc2015.day10

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2015.day10.part1.lookAnsSayLength
import java.io.InputStream

private const val PART_2_ITERATIONS = 50

object Day10 : Puzzle {
    override val name: String = "Elves Look, Elves Say"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day10/look-and-say.txt"

    override fun part1(input: InputStream) =
        lookAnsSayLength(input.bufferedReader().lineSequence().first())

    // Code from part1 fully fits!
    override fun part2(input: InputStream) =
        lookAnsSayLength(input.bufferedReader().lineSequence().first(), PART_2_ITERATIONS)
}