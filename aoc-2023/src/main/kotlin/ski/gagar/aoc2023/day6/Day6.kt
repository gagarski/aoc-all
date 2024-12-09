package ski.gagar.aoc2023.day6

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.readTextAndClose
import java.io.InputStream

object Day6 : Puzzle {
    override val name: String = "Wait For It"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day6/boat-races.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2023.day6.part1.multiplyOfNWinningOptions(input.bufferedReader().readTextAndClose())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2023.day6.part2.multiplyOfNWinningOptions(input.bufferedReader().readTextAndClose())
}