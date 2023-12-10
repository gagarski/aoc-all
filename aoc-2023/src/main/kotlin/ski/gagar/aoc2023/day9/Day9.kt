package ski.gagar.aoc2023.day9

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day9 : Puzzle {
    override val name: String = "Mirage Maintenance"
    override val inputPath: String = "/ski.gagar.aoc.aoc2023.day9/oasis.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2023.day9.part1.sumExtrapolated(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2023.day9.part2.sumExtrapolated(input.bufferedReader().lineSequence())
}