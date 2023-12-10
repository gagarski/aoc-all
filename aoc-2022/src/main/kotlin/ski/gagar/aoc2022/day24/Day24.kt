package ski.gagar.aoc2022.day24

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day24 : Puzzle {
    override val name: String = "Blizzard Basin"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day24/blizzards.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day24.part1.findQuickestLength(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day24.part2.findQuickestLength(input.bufferedReader().lineSequence())
}