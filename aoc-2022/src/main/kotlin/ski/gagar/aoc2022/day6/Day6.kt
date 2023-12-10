package ski.gagar.aoc2022.day6

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc2022.day6.part1.startOffset
import java.io.InputStream

private const val N_DIFFERENT_PART_2 = 14

object Day6 : Puzzle {
    override val name: String = "Tuning Trouble"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day6/messages.txt"

    override fun part1(input: InputStream) = startOffset(input.bufferedReader().lineSequence().first())

    override fun part2(input: InputStream) = startOffset(input.bufferedReader().lineSequence().first(), N_DIFFERENT_PART_2)
}