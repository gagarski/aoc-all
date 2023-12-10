package ski.gagar.aoc2022.day9

import ski.gagar.aoc.util.Puzzle
import java.io.InputStream

object Day9 : Puzzle {
    override val name: String = "Rope Bridge"
    override val inputPath: String = "/ski.gagar.aoc.aoc2022.day9/bridge.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2022.day9.part1.doMoves(input.bufferedReader().lineSequence())

    override fun part2(input: InputStream) =
        ski.gagar.aoc2022.day9.part2.doMoves(input.bufferedReader().lineSequence())
}