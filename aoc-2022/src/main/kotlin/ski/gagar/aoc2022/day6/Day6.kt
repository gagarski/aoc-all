package ski.gagar.aoc2022.day6

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import ski.gagar.aoc2022.day6.part1.startOffset
import java.io.InputStream

private const val N_DIFFERENT_PART_2 = 14

object Day6 : Puzzle {
    override val name: String = "Tuning Trouble"

    override fun part1(input: InputStream) = startOffset(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) = startOffset(input.bufferedReader().readTextAndClose(), N_DIFFERENT_PART_2)
}